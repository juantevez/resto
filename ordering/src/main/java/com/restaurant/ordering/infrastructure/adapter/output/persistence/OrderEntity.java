package com.restaurant.ordering.infrastructure.adapter.output.persistence;

import com.restaurant.ordering.domain.model.Order;
import com.restaurant.ordering.domain.model.OrderStatus;
import com.restaurant.shared.domain.valueobjects.OrderId;
import com.restaurant.shared.domain.valueobjects.TableId;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private String status;

    @OneToMany(
            mappedBy = "order", // Este nombre debe coincidir con el campo en OrderItemEntity
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItemEntity> items;

    /**
     * Helper para mantener la consistencia bidireccional.
     */
    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    public Order toDomain() {
        return new Order(
                new OrderId(this.id),
                new TableId(this.tableId),
                this.items.stream()
                        .map(OrderItemEntity::toDomain)
                        .collect(Collectors.toList()),
                OrderStatus.fromString(this.statusType)
        );
    }

    public static OrderEntity fromDomain(Order order) {
        OrderEntity entity = OrderEntity.builder()
                .id(order.id().value())
                .tableId(order.tableId().value())
                .statusType(order.status().getClass().getSimpleName().toUpperCase())
                .statusTimestamp(LocalDateTime.now())
                .build();

        // Mapeamos los items y les asignamos la entidad padre manualmente
        List<OrderItemEntity> itemEntities = order.items().stream()
                .map(item -> {
                    OrderItemEntity itemEntity = OrderItemEntity.fromDomain(item, entity); // <--- Ahora sí tenés 'entity'
                    return itemEntity;
                }).toList();

        entity.setItems(itemEntities);
        return entity;
    }
}
