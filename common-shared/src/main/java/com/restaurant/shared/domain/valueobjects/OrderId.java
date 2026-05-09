package com.restaurant.shared.domain.valueobjects;

import java.util.UUID;

/**
 * Value Object para la identidad de un Pedido (Order).
 * Al ser un record, obtenemos inmutabilidad, equals() y hashCode() gratis.
 */
public record OrderId(UUID value) {

    // Constructor compacto para validaciones
    public OrderId {
        if (value == null) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo");
        }
    }

    /**
     * Factory method para crear un ID nuevo aleatorio.
     */
    public static OrderId random() {
        return new OrderId(UUID.randomUUID());
    }

    /**
     * Factory method para recrear un ID desde un String (ej. desde un UUID de la DB).
     */
    public static OrderId fromString(String uuid) {
        return new OrderId(UUID.fromString(uuid));
    }

    /**
     * Factory method para recrear un ID desde un UUID.
     */
    public static OrderId fromUUID(UUID uuid) {
        return new OrderId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
