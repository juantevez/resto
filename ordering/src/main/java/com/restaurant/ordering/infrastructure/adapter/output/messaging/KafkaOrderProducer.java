package com.restaurant.ordering.infrastructure.adapter.output.messaging;

import com.restaurant.ordering.application.ports.output.OrderEventPublisher;
import com.restaurant.ordering.domain.model.Order;
import com.restaurant.ordering.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaOrderProducer implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "restaurant.orders.created";

    @Override
    public void publishOrderCreated(Order order) {
        // 1. Mapeamos el dominio al evento de infraestructura
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.id().value(),
                order.tableId().value(),
                order.totalAmount(),
                order.items().stream()
                        .map(item -> item.quantity() + "x " + item.productName())
                        .collect(Collectors.toList()),
                LocalDateTime.now().toString()
        );

        log.info("Publicando evento de orden creada: {}", event.orderId());

        // 2. Enviamos a Kafka
        // Gracias a Virtual Threads, si el buffer de Kafka está lleno,
        // el hilo se suspende sin bloquear el carrier thread.
        kafkaTemplate.send(TOPIC, event.orderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Evento enviado con éxito al tópico {} [Offset: {}]",
                                TOPIC, result.getRecordMetadata().offset());
                    } else {
                        log.error("Error al enviar evento a Kafka: {}", ex.getMessage());
                        // Aquí podrías implementar una política de reintentos o una Outbox Table
                    }
                });
    }
}
