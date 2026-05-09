package com.restaurant.table.infrastructure.adapter.input.rest;

import com.restaurant.table.application.usecase.ManageTableRestaurantService;
import com.restaurant.table.domain.model.TableRestaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurant-tables")
@RequiredArgsConstructor
public class TableRestaurantController {

    private final ManageTableRestaurantService manageTableService;

    @PostMapping("/{id}/occupy")
    public ResponseEntity<TableRestaurant> occupy(
            @PathVariable UUID id,
            @RequestParam UUID orderId) {

        // Gracias a la config de Virtual Threads, cada request aquí
        // corre en un hilo liviano.
        return ResponseEntity.ok(manageTableService.occupyTable(id, orderId));
    }
}
