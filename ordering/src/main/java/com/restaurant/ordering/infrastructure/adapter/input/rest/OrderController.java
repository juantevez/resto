package com.restaurant.ordering.infrastructure.adapter.input.rest;

import com.restaurant.ordering.application.usecase.PlaceOrderUseCase;
import com.restaurant.ordering.domain.model.Order;
import com.restaurant.ordering.domain.model.OrderItem;
import com.restaurant.shared.domain.valueobjects.ProductId;
import com.restaurant.shared.domain.valueobjects.TableId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody CreateOrderRequest request) {
        // Convertimos los DTOs de entrada a objetos de dominio
        List<OrderItem> domainItems = request.items().stream()
                .map(item -> new OrderItem(
                        //item.productId(),
                        new ProductId(item.productId()),
                        item.productName(),
                        item.quantity(),
                        item.unitPrice()
                )).toList();

        // Ejecutamos el caso de uso
        Order order = placeOrderUseCase.execute(
                TableId.fromUUID(request.tableId()),
                domainItems
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OrderResponse(order.id().value(), "ORDER_PLACED", order.totalAmount()));
    }

    // DTOs internos para el Controller
    public record CreateOrderRequest(UUID tableId, List<OrderItemRequest> items) {}
    public record OrderItemRequest(UUID productId, String productName, int quantity, java.math.BigDecimal unitPrice) {}
    public record OrderResponse(UUID orderId, String status, java.math.BigDecimal total) {}
}
