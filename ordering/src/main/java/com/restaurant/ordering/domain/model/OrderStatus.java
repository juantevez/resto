package com.restaurant.ordering.domain.model;

import java.time.LocalDateTime;

/**
 * Representa los estados posibles de un pedido.
 * Al ser sealed, el compilador garantiza que solo estos registros pueden
 * implementar la interfaz, lo que hace el código más seguro.
 */
public sealed interface OrderStatus
        permits OrderStatus.Pending,
        OrderStatus.Confirmed,
        OrderStatus.Ready,
        OrderStatus.Delivered,
        OrderStatus.Cancelled {

    // Registro para pedidos recién creados pero no confirmados
    record Pending(LocalDateTime createdAt) implements OrderStatus {
        public Pending() { this(LocalDateTime.now()); }
    }

    // El pedido ya fue validado y enviado a cocina
    record Confirmed(LocalDateTime confirmedAt) implements OrderStatus {
        public Confirmed() { this(LocalDateTime.now()); }
    }

    // La cocina terminó el plato y está listo para retirar
    record Ready(LocalDateTime readyAt) implements OrderStatus {
        public Ready() { this(LocalDateTime.now()); }
    }

    // El cliente ya recibió su pedido
    record Delivered(LocalDateTime deliveredAt) implements OrderStatus {
        public Delivered() { this(LocalDateTime.now()); }
    }

    // El pedido fue anulado (incluimos el motivo por trazabilidad)
    record Cancelled(LocalDateTime cancelledAt, String reason) implements OrderStatus {
        public Cancelled(String reason) { this(LocalDateTime.now(), reason); }
    }
    // Método estático para convertir String -> Objeto de estado
    static OrderStatus fromString(String status) {
        return switch (status.toUpperCase()) {
            case "PENDING" -> new Pending();
            case "CONFIRMED" -> new Confirmed();
            case "READY" -> new Ready();
            case "DELIVERED" -> new Delivered();
            case "CANCELLED" -> new Cancelled("Restored from database");
            default -> throw new IllegalArgumentException("Estado desconocido: " + status);
        };
    }

    // Método para obtener el nombre (útil para guardar en DB)
    default String name() {
        return this.getClass().getSimpleName().toUpperCase();
    }
}
