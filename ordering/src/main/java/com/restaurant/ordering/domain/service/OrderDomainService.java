package com.restaurant.ordering.domain.service;

import com.restaurant.ordering.domain.model.Order;
import com.restaurant.ordering.domain.model.OrderItem;
import com.restaurant.ordering.domain.model.OrderStatus;
import java.util.List;

public class OrderDomainService {

    public Order createOrder(Order order) {
        if (order.items().isEmpty()) {
            throw new IllegalArgumentException("No se puede crear un pedido sin ítems");
        }
        return order;
    }

    public Order transitionToConfirmed(Order order) {
        if (!(order.status() instanceof OrderStatus.Pending)) {
            throw new IllegalStateException("Solo se pueden confirmar pedidos pendientes");
        }
        return new Order(order.id(), order.tableId(), order.items(), new OrderStatus.Confirmed());
    }
}
