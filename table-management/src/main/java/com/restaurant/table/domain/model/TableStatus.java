package com.restaurant.table.domain.model;

import java.util.UUID;

public sealed interface TableStatus {

    // Al estar dentro de la interfaz, pueden ser public
    // o simplemente omitir el modificador (son public por defecto)
    record Available() implements TableStatus {}

    record Occupied(UUID orderId) implements TableStatus {}

    record Reserved(String customerName) implements TableStatus {}

    record OutOfService(String reason) implements TableStatus {}
}