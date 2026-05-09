package com.restaurant.table.domain.repository;


import com.restaurant.table.domain.model.TableRestaurant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TableRestaurantRepository {
    Optional<TableRestaurant> findById(UUID id);
    List<TableRestaurant> findAll();
    TableRestaurant save(TableRestaurant table);
}

