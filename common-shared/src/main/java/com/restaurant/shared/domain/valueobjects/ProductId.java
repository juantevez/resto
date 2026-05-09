package com.restaurant.shared.domain.valueobjects;

import java.util.UUID;
import java.util.Objects;

public record ProductId(UUID value) {
    public ProductId {
        Objects.requireNonNull(value, "El ID del producto no puede ser nulo");
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
