package dev.shopsphere.inventory.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String customerId,
        String currency,
        List<Item> items,
        Instant occurredAt
) {

    public record Item(
            UUID productId,
            int quantity
    ) {
    }
}