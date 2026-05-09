package com.restaurant.table.infrastructure.adapter.output.persistence;

import com.restaurant.shared.domain.valueobjects.TableId;
import com.restaurant.table.domain.model.TableRestaurant;
import com.restaurant.table.domain.model.TableStatus;
// Importamos explícitamente tu Record de dominio

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
// Mantenemos el import de JPA
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
// Aquí el compilador sabe que es la anotación de Jakarta
@Table(name = "tables")
@Getter
@Setter
public class TableRestaurantEntity {
    @Id
    private UUID id;
    private int number;
    private int capacity;
    private String statusType;
    private UUID currentOrderId;

    public TableRestaurant toDomain() {
        TableStatus status = switch (statusType) {
            case "OCCUPIED" -> new TableStatus.Occupied(currentOrderId);
            case "RESERVED" -> new TableStatus.Reserved("Guest");
            default -> new TableStatus.Available();
        };

        // Envolvemos el UUID en el Value Object TableId
        return new TableRestaurant(
                new TableId(this.id),
                this.capacity,
                this.number,
                status
        );
    }

    public static TableRestaurantEntity fromDomain(TableRestaurant table) {
        TableRestaurantEntity entity = new TableRestaurantEntity();

        // Mapeo de datos básicos
        entity.setId(table.id().value());
        entity.setNumber(table.number());
        entity.setCapacity(table.capacity());

        // Pattern Matching para el sellado de TableStatus (Java 21)
        String type = switch (table.status()) {
            case TableStatus.Available() -> "AVAILABLE";

            case TableStatus.Occupied(var orderId) -> {
                entity.setCurrentOrderId(orderId);
                yield "OCCUPIED";
            }

            case TableStatus.Reserved(var name) -> {
                // Podrías guardar el nombre en una columna extra si quisieras
                yield "RESERVED";
            }

            case TableStatus.OutOfService(var reason) -> "OUT_OF_SERVICE";
        };

        entity.setStatusType(type);
        return entity;
    }
}