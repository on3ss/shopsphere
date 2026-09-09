package dev.shopsphere.product.product;

import java.math.BigDecimal;

public record CreateProductRequest(
        String name,
        String description,
        BigDecimal price,
        String currency) {

}
