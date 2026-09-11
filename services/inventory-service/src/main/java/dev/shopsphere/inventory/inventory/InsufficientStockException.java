package dev.shopsphere.inventory.inventory;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(
            UUID productId,
            int requested,
            int available
    ) {
        super(
                "Insufficient inventory for product %s. Requested: %d, available: %d"
                        .formatted(productId, requested, available)
        );
    }
}
