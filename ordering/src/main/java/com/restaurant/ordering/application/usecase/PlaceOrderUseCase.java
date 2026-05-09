package com.restaurant.ordering.application.usecase;

import com.restaurant.ordering.domain.model.Order;
import com.restaurant.ordering.domain.model.OrderItem;
import com.restaurant.ordering.domain.model.OrderStatus;
import com.restaurant.ordering.application.ports.output.OrderRepository;
import com.restaurant.ordering.application.ports.output.OrderEventPublisher;
import com.restaurant.shared.domain.valueobjects.OrderId;
import com.restaurant.shared.domain.valueobjects.TableId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public Order execute(TableId tableId, List<OrderItem> items) {
        // 1. Crear el objeto de dominio
        Order order = new Order(
                OrderId.random(),
                tableId,
                items,
                new OrderStatus.Pending()
        );

        // 2. Persistir (Bloqueante, pero manejado por Virtual Thread)
        Order savedOrder = orderRepository.save(order);

        // 3. Notificar a otros módulos (Kafka)
        eventPublisher.publishOrderCreated(savedOrder);

        return savedOrder;
    }
}
