package com.restaurant.table.infrastructure.adapter.output.persistence;

import com.restaurant.table.domain.model.TableRestaurant;
import com.restaurant.table.domain.repository.TableRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TableRestaurantPersistenceAdapter implements TableRestaurantRepository {

    private final TableRestaurantJpaRepository jpaRepository;

    @Override
    public Optional<TableRestaurant> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(TableRestaurantEntity::toDomain);
    }

    @Override
    public List<TableRestaurant> findAll() {
        return jpaRepository.findAll().stream()
                .map(TableRestaurantEntity::toDomain)
                .toList();
    }

    @Override
    public TableRestaurant save(TableRestaurant table) {
        // Necesitamos un método estático o constructor en TableEntity
        // para convertir desde el dominio a la entidad
        TableRestaurantEntity entity = TableRestaurantEntity.fromDomain(table);
        return jpaRepository.save(entity).toDomain();
    }
}
