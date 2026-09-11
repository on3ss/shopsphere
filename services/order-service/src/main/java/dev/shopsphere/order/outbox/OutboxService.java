package dev.shopsphere.order.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shopsphere.order.event.OrderCreatedEvent;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxService(
            OutboxEventRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void store(OrderCreatedEvent event) {

        try {
            String payload =
                    objectMapper.writeValueAsString(event);

            repository.save(
                    new OutboxEvent(
                            event.eventId(),
                            "Order",
                            event.orderId(),
                            "OrderCreated",
                            payload
                    )
            );

        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Failed to serialize OrderCreated event",
                    ex
            );
        }
    }
}