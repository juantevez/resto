package com.restaurant.ordering.application.ports.output;

import com.restaurant.ordering.domain.model.Order;

public interface OrderEventPublisher {
    void publishOrderCreated(Order order);
}
