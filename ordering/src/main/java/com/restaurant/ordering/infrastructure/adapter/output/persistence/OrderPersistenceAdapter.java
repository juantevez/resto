package com.restaurant.ordering.infrastructure.adapter.output.persistence;

import com.restaurant.ordering.application.ports.output.OrderRepository;
import com.restaurant.ordering.domain.model.Order;

import com.restaurant.ordering.infrastructure.adapter.output.persistence.repository.JpaOrderRepository;
import com.restaurant.shared.domain.valueobjects.OrderId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderPersistenceAdapter implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    public OrderPersistenceAdapter(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
    }

    @Override
    public Order save(Order order) {
        // 1. Mapear de Dominio a Entidad (importante en Hexagonal)
        OrderEntity entity = OrderEntity.fromDomain(order);

        // 2. Guardar en Postgres
        OrderEntity savedEntity = jpaOrderRepository.save(entity);

        // 3. Devolver el objeto de Dominio mapeado
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        // Buscamos en el repo de JPA usando el UUID que viene dentro del Value Object OrderId
        return jpaOrderRepository.findById(id.value())
                .map(OrderEntity::toDomain); // Si existe, lo mapeamos a dominio
    }
}
