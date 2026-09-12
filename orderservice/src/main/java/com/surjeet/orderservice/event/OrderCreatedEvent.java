package com.surjeet.orderservice.event;

public record OrderCreatedEvent(

        Integer orderId,
        Integer productId,
        String productName,
        Integer quantity,
        Double totalAmount

) {
}