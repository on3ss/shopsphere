package dev.shopsphere.order.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt,
        Instant updatedAt,
        List<Item> items
) {

    public record Item(
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            String currency,
            int quantity,
            BigDecimal subtotal
    ) {
    }
}