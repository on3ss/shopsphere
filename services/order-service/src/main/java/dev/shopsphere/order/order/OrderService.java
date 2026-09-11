package dev.shopsphere.order.order;

import dev.shopsphere.order.event.OrderCreatedEvent;
import dev.shopsphere.order.product.ProductClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(
            OrderRepository orderRepository,
            ProductClient productClient) {

        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    public OrderResponse create(CreateOrderRequest request) {

        Order order = new Order(
                request.customerId(),
                request.currency()
        );

        for (CreateOrderRequest.Item requestedItem : request.items()) {

            ProductClient.Product product =
                    productClient.getProduct(requestedItem.productId());

            if (!product.active()) {
                throw new InvalidOrderException(
                        "Product is inactive: " + product.id()
                );
            }

            if (!product.currency().equalsIgnoreCase(request.currency())) {
                throw new InvalidOrderException(
                        "Product currency does not match order currency: "
                                + product.id()
                );
            }

            OrderItem item = new OrderItem(
                    product.id(),
                    product.name(),
                    product.price(),
                    product.currency(),
                    requestedItem.quantity()
            );

            order.addItem(item);
        }

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                order.getId(),
                order.getCustomerId(),
                order.getCurrency(),
                order.getItems()
                        .stream()
                        .map(orderItem -> new OrderCreatedEvent.Item(
                                orderItem.getProductId(),
                                orderItem.getQuantity())
                        ).toList(),
                Instant.now()
        );

        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(UUID id) {

        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderResponse toResponse(Order order) {

        List<OrderResponse.Item> items = order.getItems()
                .stream()
                .map(item -> new OrderResponse.Item(
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getCurrency(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }
}