package dev.shopsphere.order.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {

    private static final String TOPIC = "order.events";

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(
            OutboxEventRepository repository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publish() {

        var events =
                repository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {

            kafkaTemplate.send(
                    TOPIC,
                    event.getAggregateId().toString(),
                    event.getPayload()
            );

            event.markPublished();
        }

        repository.saveAll(events);
    }
}