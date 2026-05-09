# 🍴 Restaurant Management System (Hexagonal & Virtual Threads)

Este proyecto es una implementación de microservicios diseñada para demostrar el uso de **Arquitectura Hexagonal (Ports & Adapters)**, comunicación asincrónica mediante **Apache Kafka** y alta concurrencia utilizando **Java 21 Virtual Threads (Project Loom)**.

## 🚀 Arquitectura del Sistema

El sistema sigue los principios de **Domain-Driven Design (DDD)**, aislando la lógica de negocio de las dependencias externas (bases de datos, brokers de mensajería, APIs).

### Módulos Principales:
1.  **`common-shared`**: Contiene records y objetos compartidos (DTOs/Eventos) para evitar la duplicación de código.
2.  **`table-management`**: Gestión del estado de las mesas del restaurante.
3.  **`ordering`**: Núcleo del sistema. Recibe pedidos, los persiste en PostgreSQL y publica eventos en Kafka.
4.  **`kitchen`**: Consumidor de eventos. Recibe pedidos de Kafka y simula el proceso de preparación utilizando **Virtual Threads**.

---

## 🛠️ Stack Tecnológico

*   **Lenguaje:** Java 21 (Temurin SDK).
*   **Framework:** Spring Boot 3.2.x.
*   **Persistencia:** PostgreSQL 15.
*   **Mensajería:** Apache Kafka 3.7.0 (Imagen oficial).
*   **Contenedores:** Docker & Docker Compose.
*   **Monitoreo Kafka:** Kafdrop (UI para inspección de tópicos en el puerto 9000).

---

## 🏗️ Guía de Instalación y Uso

### 1. Requisitos Previos
*   Docker y Docker Compose instalados (Probado en **Ubuntu 24.04**).
*   Maven 3.9+.

### 2. Compilación del Proyecto
Desde la raíz del proyecto, generá los artefactos ejecutables:
```bash
mvn clean package -DskipTests
```

### 3. Despliegue con Docker
Levanta toda la infraestructura y los microservicios:

```bash
docker compose up -d --build
```
### 4. Verificación de Servicios
API Ordering: http://localhost:8082
API Kitchen: http://localhost:8083
Kafdrop (Kafka UI): http://localhost:9000

### Flujo de Eventos (Event-Driven)
El cliente realiza un POST /api/v1/orders al servicio Ordering.

Ordering persiste la orden y emite un evento OrderCreatedEvent al tópico restaurant.orders.created.

Kitchen consume el evento y dispara un Virtual Thread que simula la preparación (bloqueo de 3 segundos).

El sistema loguea la finalización sin bloquear hilos del sistema operativo.

🧵 Implementación de Virtual Threads
El proyecto está optimizado para manejar alta concurrencia con un consumo mínimo de recursos. En el application.yml de cada microservicio se activa:

```bash
spring:
  threads:
    virtual:
      enabled: true
```

Esto permite que operaciones bloqueantes (como el Thread.sleep en la simulación de cocina o las consultas a la base de datos) no saturen el pool de hilos Carrier, escalando de forma masiva.

### Estructura de Paquetes (Hexagonal)
Cada módulo se organiza de la siguiente manera:
#### domain: Entidades y reglas de negocio.
#### application: Casos de uso e interfaces de puertos.
#### infrastructure:
#### adapters: Implementaciones de persistencia (Postgres) y mensajería (Kafka).
#### config: Definición de Beans de Spring.
#### rest: Controladores (Adaptores de entrada).

###  Ejemplo de Prueba (CURL)
Ejecutá este comando en tu terminal para simular un pedido real:

```bash
curl -X POST http://localhost:8082/api/v1/orders \
-H "Content-Type: application/json" \
-d '{
    "tableId": "'$(uuidgen)'",
    "items": [
        {"productId": "'$(uuidgen)'", "productName": "Hamburguesa Senior", "quantity": 1, "unitPrice": 12.50},
        {"productId": "'$(uuidgen)'", "productName": "Cerveza Artesanal", "quantity": 2, "unitPrice": 5.00}
    ]
}'
```