package com.restaurant.table.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TableRestaurantJpaRepository extends JpaRepository<TableRestaurantEntity, UUID> {
    // Aquí podrías agregar métodos como findByNumber si lo necesitas
}
