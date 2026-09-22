package com.surjeet.paymentservice.event;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentDltConsumer {

    @KafkaListener(
            topics = "order-created.DLT",
            groupId = "payment-dlt-group"
    )
    public void consumeDeadLetterEvent(
            ConsumerRecord<String, OrderCreatedEvent> record
    ) {

        OrderCreatedEvent event = record.value();

        System.err.println(
                "DLT event received"
        );

        System.err.println(
                "Event ID: " + event.eventId()
        );

        System.err.println(
                "Order ID: " + event.orderId()
        );

        System.err.println(
                "DLT Topic: " + record.topic()
        );

        System.err.println(
                "DLT Partition: " + record.partition()
        );

        System.err.println(
                "DLT Offset: " + record.offset()
        );

        record.headers().forEach(header -> {

            System.err.println(
                    "Header: "
                            + header.key()
                            + " = "
                            + new String(header.value())
            );
        });
    }
}