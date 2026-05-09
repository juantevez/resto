package com.restaurant.kitchen.domain.model;

import java.util.List;
import java.util.UUID;

public record KitchenTicket(
        UUID ticketId,
        UUID orderId,
        List<TicketItem> items,
        TicketStatus status
) {
    public enum TicketStatus { RECEIVED, PREPARING, READY }
}
