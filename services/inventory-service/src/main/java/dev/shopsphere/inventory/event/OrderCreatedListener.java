package dev.shopsphere.inventory.event;

import dev.shopsphere.inventory.inventory.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    private final InventoryService inventoryService;

    public OrderCreatedListener(
            InventoryService inventoryService
    ) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(
            topics = "order.events",
            groupId = "inventory-service"
    )
    public void handle(OrderCreatedEvent event) {

        for (OrderCreatedEvent.Item item : event.items()) {

            inventoryService.reserve(
                    item.productId(),
                    item.quantity()
            );
        }
    }
}