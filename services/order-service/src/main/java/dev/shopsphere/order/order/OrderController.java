package dev.shopsphere.order.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(
            @Valid @RequestBody CreateOrderRequest request) {

        return service.create(request);
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }
}