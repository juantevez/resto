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

    public Order toDomain() {
        return new Order(
                new OrderId(this.id),
                new TableId(this.tableId),
                this.items.stream()
                        .map(OrderItemEntity::toDomain)
                        .collect(Collectors.toList()),
                OrderStatus.fromString(this.status) // <--- Usamos el nuevo método
        );
    }

    public static OrderEntity fromDomain(Order order) {
        return OrderEntity.builder()
                .id(order.id().value())
                .tableId(order.tableId().value())
                .status(order.status().name()) // <--- Usamos el default name()
                .items(order.items().stream()
                        .map(item -> OrderItemEntity.fromDomain(item, null)) // Ajustar según tu lógica de persistencia
                        .collect(Collectors.toList()))
                .build();
    }
}
