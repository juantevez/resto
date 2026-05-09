package com.restaurant.table.application.ports.input;


import com.restaurant.table.domain.model.TableRestaurant;
import com.restaurant.table.domain.repository.TableRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageTableUseCase {
    private final TableRestaurantRepository tableRestaurantRepository;

    public TableRestaurant occupyTable(UUID tableId, UUID orderId) {
        TableRestaurant tableRestaurant = tableRestaurantRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        TableRestaurant occupiedTableRestaurant = tableRestaurant.occupy(orderId);
        return tableRestaurantRepository.save(occupiedTableRestaurant);
    }
}