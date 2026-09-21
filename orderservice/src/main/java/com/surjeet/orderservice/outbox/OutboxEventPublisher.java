package com.surjeet.orderservice.outbox;

import com.surjeet.orderservice.entity.OutboxEvent;
import com.surjeet.orderservice.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventPublisher {

    private static final String TOPIC = "order-created";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxEventPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findByStatusOrderByCreatedAtAsc("NEW");

        for (OutboxEvent event : events) {

            try {

                kafkaTemplate.send(
                        TOPIC,
                        event.getAggregateId().toString(),
                        event.getPayload()
                );

                event.setStatus("PUBLISHED");

                outboxEventRepository.save(event);

            } catch (Exception ex) {

                System.err.println(
                        "Failed to publish outbox event: "
                                + event.getId()
                );

                System.err.println(
                        "Reason: " + ex.getMessage()
                );
            }
        }
    }
}