package com.surjeet.paymentservice.event;

public record OrderCreatedEvent(

        Long eventId,
        Integer orderId,
        Integer productId,
        String productName,
        Integer quantity,
        Double totalAmount

) {
}