package com.restaurant.ordering.domain.model;

import com.restaurant.shared.domain.valueobjects.ProductId;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representa un producto específico dentro de un pedido.
 * Es un Value Object: su identidad se define por sus atributos.
 */
public record OrderItem(
        ProductId productId,      // Referencia al catálogo de productos
        String productName,  // Denormalizamos el nombre para auditoría
        int quantity,
        BigDecimal unitPrice
) {
    // Constructor compacto para validaciones de dominio
    public OrderItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (productId == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio");
        }
    }

    // Lógica de negocio derivada (Calculada al vuelo)
    public BigDecimal getSubTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
