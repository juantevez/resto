package com.restaurant.table.application.usecase;


import com.restaurant.table.domain.model.TableRestaurant;
import com.restaurant.table.domain.repository.TableRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageTableRestaurantService {

    private final TableRestaurantRepository tableRepository;

    @Transactional
    public TableRestaurant occupyTable(UUID tableId, UUID orderId) {
        TableRestaurant table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        TableRestaurant occupiedTable = table.occupy(orderId);
        return tableRepository.save(occupiedTable);
    }
}