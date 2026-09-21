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

        paymentService.processPayment(
                event.orderId(),
                event.totalAmount()
        );

        System.out.println(
                "Payment processed successfully for order: "
                        + event.orderId()
        );
    }
}

/*
What happens now?
Kafka receives JSON:

{
  "orderId": 1,
  "productId": 10,
  "productName": "Laptop",
  "quantity": 2,
  "totalAmount": 100000.0
}
        ↓ JsonDeserializer

OrderCreatedEvent object
        ↓
@KafkaListener
        ↓
PaymentService.processPayment(1, 100000.0)
        ↓
Payment saved in postgres
*/