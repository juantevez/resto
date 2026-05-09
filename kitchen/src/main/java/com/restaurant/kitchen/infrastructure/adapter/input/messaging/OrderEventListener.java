package com.restaurant.kitchen.infrastructure.adapter.input.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    @KafkaListener(topics = "restaurant.orders.created", groupId = "kitchen-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("👨‍🍳 Cocina recibió el pedido: {} para la mesa {}",
                event.orderId(), event.tableId());

        // Aquí llamaríamos al UseCase para crear el ticket
        log.info("Preparando: {}", event.items());
    }
}

// El DTO debe coincidir con el que enviamos en Ordering
record OrderCreatedEvent(java.util.UUID orderId, java.util.UUID tableId, java.util.List<String> items) {}
