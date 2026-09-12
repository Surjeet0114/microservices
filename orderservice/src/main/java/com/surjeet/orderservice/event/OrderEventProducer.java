package com.surjeet.orderservice.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }
    /*
    KafkaTemplate<String, OrderCreatedEvent>
    String              → Kafka message key
    OrderCreatedEvent   → Kafka message value
    */

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {

        kafkaTemplate.send(
                ORDER_CREATED_TOPIC,
                event.orderId().toString(),
                event
        );

        /*
        Topic → order-created
        Key   → orderId
        Value → OrderCreatedEvent converted to JSON
         */

        System.out.println(
                "OrderCreatedEvent sent to Kafka: " + event
        );
    }
}