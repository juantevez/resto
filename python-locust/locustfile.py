"""
=============================================================
  Load Test – Ordering Service (Virtual Threads)
  Target: POST http://localhost:8082/api/v1/orders
=============================================================

Instalación:
    pip install locust faker

Ejecución – UI web (recomendada para ver gráficas en vivo):
    locust -f locustfile.py --host=http://localhost:8082

Ejecución – headless (CI / terminal):
    locust -f locustfile.py \
           --host=http://localhost:8082 \
           --headless \
           --users 200 \
           --spawn-rate 20 \
           --run-time 2m \
           --html=report.html

Escenarios incluidos:
  · OrderingUser  – POST /api/v1/orders  (carga principal, weight=10)
  · HealthUser    – GET  /actuator/health (baseline silencioso, weight=1)
=============================================================
"""

import uuid
import random
from locust import HttpUser, task, between, events
from faker import Faker

fake = Faker("es_AR")

# ------------------------------------------------------------------
# Catálogo de productos de prueba
# ------------------------------------------------------------------
PRODUCTS = [
    {"name": "Hamburguesa Tech",    "price": 1500.00},
    {"name": "Pizza Microservicio", "price": 2200.00},
    {"name": "Milanesa DDD",        "price": 1800.00},
    {"name": "Ensalada Kafka",      "price": 950.00},
    {"name": "Gaseosa Spring",      "price": 450.00},
    {"name": "Papas Virtual",       "price": 700.00},
    {"name": "Postre Hexagonal",    "price": 1100.00},
]

# ------------------------------------------------------------------
# Pool de mesas fijas (simula mesas reales del restaurante)
# ------------------------------------------------------------------
TABLE_IDS = [str(uuid.uuid4()) for _ in range(20)]


def build_order_payload(num_items: int = None) -> dict:
    """Construye un payload realista para POST /api/v1/orders."""
    if num_items is None:
        num_items = random.randint(1, 4)

    items = []
    selected = random.sample(PRODUCTS, k=min(num_items, len(PRODUCTS)))
    for product in selected:
        items.append({
            "productId":   str(uuid.uuid4()),
            "productName": product["name"],
            "quantity":    random.randint(1, 3),
            "unitPrice":   product["price"],
        })

    return {
        "tableId": random.choice(TABLE_IDS),
        "items":   items,
    }


# ------------------------------------------------------------------
# Usuario principal – simula mozos tomando pedidos
# ------------------------------------------------------------------
class OrderingUser(HttpUser):
    """
    Simula la carga real del endpoint de órdenes.
    wait_time: pausa entre requests (simula el tiempo entre pedidos reales).
    """
    weight = 10
    wait_time = between(0.5, 2.0)  # segundos entre requests

    @task(8)
    def place_single_item_order(self):
        """Pedido simple – caso más frecuente."""
        payload = build_order_payload(num_items=1)
        with self.client.post(
            "/api/v1/orders",
            json=payload,
            catch_response=True,
            name="POST /orders [1 item]",
        ) as response:
            _validate_response(response, expected_status=201)

    @task(5)
    def place_multi_item_order(self):
        """Pedido con varios ítems – caso moderado."""
        payload = build_order_payload(num_items=random.randint(2, 4))
        with self.client.post(
            "/api/v1/orders",
            json=payload,
            catch_response=True,
            name="POST /orders [multi item]",
        ) as response:
            _validate_response(response, expected_status=201)

    @task(2)
    def place_large_order(self):
        """Pedido grande – estressa el cálculo de totalAmount y la persistencia."""
        payload = build_order_payload(num_items=len(PRODUCTS))
        with self.client.post(
            "/api/v1/orders",
            json=payload,
            catch_response=True,
            name="POST /orders [full catalog]",
        ) as response:
            _validate_response(response, expected_status=201)

    @task(1)
    def place_invalid_order(self):
        """
        Payload inválido – valida que el servidor no se rompa bajo error handling.
        Se marca como success en Locust aunque devuelva 4xx (es comportamiento esperado).
        """
        payload = {"tableId": str(uuid.uuid4()), "items": []}
        with self.client.post(
            "/api/v1/orders",
            json=payload,
            catch_response=True,
            name="POST /orders [invalid - empty items]",
        ) as response:
            # 4xx es esperado; lo marcamos success para no distorsionar la tasa de error
            if response.status_code in (400, 422, 500):
                response.success()
            else:
                response.failure(f"Expected 4xx, got {response.status_code}")


# ------------------------------------------------------------------
# Usuario de health check – baseline silencioso
# ------------------------------------------------------------------
class HealthUser(HttpUser):
    """
    Llama al actuator para medir la latencia base del servidor
    sin lógica de negocio. Útil para comparar con OrderingUser.
    """
    weight = 1
    wait_time = between(2.0, 5.0)

    @task
    def health_check(self):
        with self.client.get(
            "/actuator/health",
            catch_response=True,
            name="GET /actuator/health",
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Health check failed: {response.status_code}")


# ------------------------------------------------------------------
# Helpers
# ------------------------------------------------------------------
def _validate_response(response, expected_status: int):
    """Valida status y que el body tenga orderId."""
    if response.status_code != expected_status:
        response.failure(
            f"Expected {expected_status}, got {response.status_code} – {response.text[:200]}"
        )
        return

    try:
        body = response.json()
        if "orderId" not in body:
            response.failure(f"Response missing 'orderId': {body}")
        else:
            response.success()
    except Exception as e:
        response.failure(f"Invalid JSON: {e}")


# ------------------------------------------------------------------
# Hook: resumen en consola al terminar el test
# ------------------------------------------------------------------
@events.quitting.add_listener
def on_quitting(environment, **kwargs):
    stats = environment.stats
    total = stats.total
    print("\n" + "=" * 60)
    print("  RESUMEN FINAL – Virtual Threads Load Test")
    print("=" * 60)
    print(f"  Total requests  : {total.num_requests}")
    print(f"  Failures        : {total.num_failures}")
    print(f"  Avg latency     : {total.avg_response_time:.1f} ms")
    print(f"  Median latency  : {total.median_response_time:.1f} ms")
    print(f"  95th percentile : {total.get_response_time_percentile(0.95):.1f} ms")
    print(f"  99th percentile : {total.get_response_time_percentile(0.99):.1f} ms")
    print(f"  Max latency     : {total.max_response_time:.1f} ms")
    print(f"  Requests/sec    : {total.current_rps:.1f}")
    print("=" * 60 + "\n")
