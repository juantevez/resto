package com.restaurant.ordering.infrastructure.adapter.output.messaging;

import com.restaurant.ordering.application.ports.output.OrderEventPublisher;
import com.restaurant.ordering.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaOrderProducer implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "restaurant.orders.created";
    private static final int MAX_RETRIES = 3;

    @Override
    public void publishOrderCreated(Order order) {
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

        kafkaTemplate.send(TOPIC, event.orderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        handlePublishFailure(event, ex, 1);
                        return;
                    }
                    var metadata = result.getRecordMetadata();
                    log.info("Orden [{}] → partition={} offset={}",
                            event.orderId(),
                            metadata.partition(),
                            metadata.offset());
                });
    }

    private void handlePublishFailure(OrderCreatedEvent event, Throwable ex, int attempt) {
        log.error("Intento {}/{} fallido para orden [{}]: {}",
                attempt, MAX_RETRIES, event.orderId(), ex.getMessage());

        if (attempt >= MAX_RETRIES) {
            log.error("Orden [{}] no pudo publicarse tras {} intentos. Requiere intervención manual.",
                    event.orderId(), MAX_RETRIES);
            // Aquí iría: guardar en Outbox table, enviar a DLQ, métricas, alertas, etc.
            return;
        }

        // Reintento con backoff simple (Virtual Thread se suspende, no bloquea carrier)
        try {
            Thread.sleep(Duration.ofMillis(200L * attempt));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return;
        }

        kafkaTemplate.send(TOPIC, event.orderId().toString(), event)
                .whenComplete((result, retryEx) -> {
                    if (retryEx != null) {
                        handlePublishFailure(event, retryEx, attempt + 1);
                        return;
                    }
                    log.info("Reintento exitoso para orden [{}] en intento {}",
                            event.orderId(), attempt + 1);
                });
    }
}
