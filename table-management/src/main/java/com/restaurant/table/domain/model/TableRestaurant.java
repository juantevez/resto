package com.restaurant.table.domain.model;


import com.restaurant.shared.domain.valueobjects.TableId;

import java.util.UUID;

public record TableRestaurant(
        TableId id,
        int capacity,
        int number,
        TableStatus status
) {
    public TableRestaurant occupy(UUID orderId) {
        if (!(status instanceof TableStatus.Available)) {
            throw new IllegalStateException("Table is not available");
        }
        return new TableRestaurant(id, capacity, number, new TableStatus.Occupied(orderId));
    }
}