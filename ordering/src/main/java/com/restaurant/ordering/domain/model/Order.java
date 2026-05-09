package com.restaurant.ordering.domain.model;

import com.restaurant.shared.domain.valueobjects.OrderId;
import com.restaurant.shared.domain.valueobjects.TableId;

import java.math.BigDecimal;
import java.util.List;

public record Order(
        OrderId id,
        TableId tableId,
        List<OrderItem> items,
        OrderStatus status
) {
    // MÉTODO DERIVADO: La fuente de verdad son los items
    public BigDecimal totalAmount() {
        return items.stream()
                .map(OrderItem::getSubTotal) // Suponiendo que OrderItem tiene getSubTotal()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Order addItem(OrderItem newItem) {
        if (!(status instanceof OrderStatus.Pending)) {
            throw new IllegalStateException("No se pueden añadir ítems a un pedido confirmado");
        }
        var updatedItems = new java.util.ArrayList<>(items);
        updatedItems.add(newItem);

        // Ya no necesitas pasar totalAmount, se calculará solo cuando se llame al método
        return new Order(id, tableId, List.copyOf(updatedItems), status);
    }
}