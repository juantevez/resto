package com.restaurant.ordering.infrastructure.adapter.output.persistence.repository;


import com.restaurant.ordering.infrastructure.adapter.output.persistence.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
}

