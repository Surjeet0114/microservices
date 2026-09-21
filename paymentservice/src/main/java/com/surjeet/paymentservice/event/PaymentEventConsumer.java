package com.surjeet.paymentservice.event;

import com.surjeet.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    private final PaymentService paymentService;

    public PaymentEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "payment-service-group"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {

        System.out.println(
                "OrderCreatedEvent received: " + event
        );

        paymentService.processOrderCreatedEvent(event);

        System.out.println(
                "Payment processing completed for event: "
                        + event.eventId()
                        + ", order: "
                        + event.orderId()
        );
    }
}