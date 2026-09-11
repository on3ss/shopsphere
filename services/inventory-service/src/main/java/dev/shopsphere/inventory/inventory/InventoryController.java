package dev.shopsphere.inventory.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/products/{productId}")
    public InventoryResponse get(
            @PathVariable UUID productId
    ) {
        return InventoryResponse.from(
                service.getByProductId(productId)
        );
    }

    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse create(
            @PathVariable UUID productId,
            @Valid @RequestBody CreateInventoryRequest request
    ) {
        return InventoryResponse.from(
                service.create(productId, request.quantity())
        );
    }

    public record CreateInventoryRequest(
            @Positive
            int quantity
    ) {
    }

    public record InventoryResponse(
            UUID productId,
            int onHand,
            int reserved,
            int available
    ) {

        static InventoryResponse from(Inventory inventory) {
            return new InventoryResponse(
                    inventory.getProductId(),
                    inventory.getOnHand(),
                    inventory.getReserved(),
                    inventory.getAvailable()
            );
        }
    }
}