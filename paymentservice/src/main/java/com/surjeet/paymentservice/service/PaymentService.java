package com.surjeet.paymentservice.service;

import com.surjeet.paymentservice.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Payment processPayment(Integer orderId, Double amount);

    Optional<Payment> getPaymentByOrderId(Integer orderId);

    List<Payment> getAllPayments();
}