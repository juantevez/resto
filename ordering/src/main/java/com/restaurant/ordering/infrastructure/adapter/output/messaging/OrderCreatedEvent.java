package com.restaurant.ordering.infrastructure.adapter.output.messaging;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public record OrderCreatedEvent(
        UUID orderId,
        UUID tableId,
        BigDecimal totalAmount,
        List<String> items,
        String createdAt
) {}

