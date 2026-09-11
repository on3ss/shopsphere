package dev.shopsphere.order.product;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductClient {

    Product getProduct(UUID productId);

    record Product(
            UUID id,
            String name,
            BigDecimal price,
            String currency,
            boolean active
    ) {
    }
}