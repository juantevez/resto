package com.restaurant.ordering.application.ports.output;

import com.restaurant.ordering.domain.model.Order;
import com.restaurant.shared.domain.valueobjects.OrderId;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
}
