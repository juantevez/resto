package com.restaurant.shared.domain.valueobjects;

import java.util.UUID;

public record TableId(UUID value) {

    // Constructor compacto para validaciones
    public TableId {
        if (value == null) {
            throw new IllegalArgumentException("El ID de la mesa no puede ser nulo");
        }
    }

    public static TableId fromUUID(UUID uuid) {
        return new TableId(uuid);
    }

    // Método estático de utilidad para generar nuevos IDs
    public static TableId nextId() {
        return new TableId(UUID.randomUUID());
    }

    // Método para facilitar la conversión desde String (útil para Controllers)
    public static TableId fromString(String uuid) {
        return new TableId(UUID.fromString(uuid));
    }
}
