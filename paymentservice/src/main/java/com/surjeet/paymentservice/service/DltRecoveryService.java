package com.surjeet.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surjeet.paymentservice.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class DltRecoveryService {

    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DltRecoveryService(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void reprocess(OrderCreatedEvent event)
            throws JsonProcessingException {

        String payload =
                objectMapper.writeValueAsString(event);

        CompletableFuture<?> future =
                kafkaTemplate.send(
                        ORDER_CREATED_TOPIC,
                        event.eventId().toString(),
                        payload
                );

        future.whenComplete((result, exception) -> {

            if (exception == null) {

                System.out.println(
                        "DLT event successfully reprocessed: "
                                + event.eventId()
                );

            } else {

                System.err.println(
                        "Failed to reprocess DLT event: "
                                + event.eventId()
                );

                System.err.println(
                        "Reason: "
                                + exception.getMessage()
                );
            }
        });
    }
}