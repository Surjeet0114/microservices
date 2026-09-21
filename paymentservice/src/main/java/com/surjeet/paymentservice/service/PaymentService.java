package com.surjeet.paymentservice.service;

import com.surjeet.paymentservice.entity.Payment;
import com.surjeet.paymentservice.event.OrderCreatedEvent;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Payment processPayment(Integer orderId, Double amount);

    Payment processOrderCreatedEvent(OrderCreatedEvent event);

    Optional<Payment> getPaymentByOrderId(Integer orderId);

    List<Payment> getAllPayments();
}