package com.surjeet.paymentservice.event;

public record OrderCreatedEvent(

        Integer orderId,
        Integer productId,
        String productName,
        Integer quantity,
        Double totalAmount

) {
}