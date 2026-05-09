package com.restaurant.ordering.infrastructure.adapter.output.persistence;

import com.restaurant.ordering.domain.model.Order;
import com.restaurant.ordering.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID tableId;

    @Column(nullable = false)
    private String statusType;

    private LocalDateTime statusTimestamp;

    private String cancellationReason;

    // Relación con los ítems.
    // Usamos orphanRemoval para que al actualizar la lista en el dominio se refleje en la DB.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();

    /**
     * Helper para mantener la consistencia bidireccional.
     */
    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    public static OrderEntity fromDomain(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.id().value());
        entity.setTableId(order.tableId().value());

        // Mapeo del OrderStatus (Sealed Interface)
        String type = switch (order.status()) {
            case OrderStatus.Pending(var time) -> {
                entity.setStatusTimestamp(time);
                yield "PENDING";
            }
            case OrderStatus.Confirmed(var time) -> {
                entity.setStatusTimestamp(time);
                yield "CONFIRMED";
            }
            case OrderStatus.Cancelled(var time, var reason) -> {
                entity.setStatusTimestamp(time);
                entity.setCancellationReason(reason);
                yield "CANCELLED";
            }
            // ... los demás estados
            default -> "UNKNOWN";
        };
        entity.setStatusType(type);

        // Mapeo de la lista de ítems
        order.items().forEach(item -> {
            OrderItemEntity itemEntity = OrderItemEntity.builder()
                    .productId(item.productId())
                    .productName(item.productName())
                    .quantity(item.quantity())
                    .unitPrice(item.unitPrice())
                    .build();
            entity.addItem(itemEntity);
        });

        return entity;
    }}
