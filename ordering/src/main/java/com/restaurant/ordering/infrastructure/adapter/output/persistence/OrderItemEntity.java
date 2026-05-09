package com.restaurant.ordering.infrastructure.adapter.output.persistence;

import com.restaurant.ordering.domain.model.OrderItem;
import com.restaurant.shared.domain.valueobjects.ProductId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false) // Aquí se define la columna física
    private OrderEntity order;

    // --- Mapeadores ---

    public OrderItem toDomain() {
        return new OrderItem(
                new ProductId(this.productId),
                this.productName,
                this.quantity,
                this.unitPrice
        );
    }

    public static OrderItemEntity fromDomain(OrderItem item, OrderEntity orderEntity) {
        return OrderItemEntity.builder()
                .productId(item.productId().getValue()) // Extraer UUID
                .productName(item.productName())
                .quantity(item.quantity())
                .unitPrice(item.unitPrice())
                .order(orderEntity) // Mantenemos la relación bidireccional si es necesario
                .build();
    }
}
