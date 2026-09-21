package com.surjeet.orderservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surjeet.orderservice.dto.OrderCreatedEvent;
import com.surjeet.orderservice.entity.OutboxEvent;
import com.surjeet.orderservice.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class OutboxEventPublisher {

    private static final String TOPIC = "order-created";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findByStatusOrderByCreatedAtAsc("NEW");

        for (OutboxEvent event : events) {

            try {

                OrderCreatedEvent orderCreatedEvent =
                        objectMapper.readValue(
                                event.getPayload(),
                                OrderCreatedEvent.class
                        );

                orderCreatedEvent.setEventId(event.getId());

                String kafkaPayload =
                        objectMapper.writeValueAsString(orderCreatedEvent);

                CompletableFuture<?> future =
                        kafkaTemplate.send(
                                TOPIC,
                                event.getAggregateId().toString(),
                                kafkaPayload
                        );

                future.whenComplete((result, exception) -> {

                    if (exception == null) {

                        event.setStatus("PUBLISHED");

                        outboxEventRepository.save(event);

                        System.out.println(
                                "Outbox event published successfully: "
                                        + event.getId()
                        );

                    } else {

                        System.err.println(
                                "Failed to publish outbox event: "
                                        + event.getId()
                        );

                        System.err.println(
                                "Reason: "
                                        + exception.getMessage()
                        );
                    }
                });

            } catch (Exception ex) {

                System.err.println(
                        "Error while publishing outbox event: "
                                + event.getId()
                );

                System.err.println(
                        "Reason: "
                                + ex.getMessage()
                );
            }
        }
    }
}