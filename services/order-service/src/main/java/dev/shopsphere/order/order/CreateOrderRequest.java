package dev.shopsphere.order.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(

        @NotBlank
        String customerId,

        @NotBlank
        String currency,

        @NotEmpty
        List<@Valid Item> items
) {

    public record Item(

            @NotNull
            UUID productId,

            @Positive
            int quantity
    ) {
    }
}