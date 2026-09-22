package com.surjeet.paymentservice.service;

import com.surjeet.paymentservice.entity.Payment;
import com.surjeet.paymentservice.entity.ProcessedEvent;
import com.surjeet.paymentservice.enums.PaymentStatus;
import com.surjeet.paymentservice.event.OrderCreatedEvent;
import com.surjeet.paymentservice.exception.InvalidOrderEventException;
import com.surjeet.paymentservice.repository.PaymentRepository;
import com.surjeet.paymentservice.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Override
    public Payment processPayment(Integer orderId, Double amount) {

        Optional<Payment> existingPayment =
                paymentRepository.findByOrderId(orderId);

        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }

        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .status(PaymentStatus.SUCCESS)
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment processOrderCreatedEvent(OrderCreatedEvent event) {

        /*
         * Check whether this Kafka event has already been processed.
         *
         * Kafka can deliver the same event more than once.
         * If the event was already processed, do not process
         * the payment again.
         */
        Optional<ProcessedEvent> existingEvent =
                processedEventRepository.findByEventId(event.eventId());

        if (event.orderId() == null ||
                event.totalAmount() == null) {

            throw new InvalidOrderEventException(
                    "Invalid order event: orderId and totalAmount are required"
            );
        }

        if (existingEvent.isPresent()) {

            System.out.println(
                    "Duplicate event ignored: "
                            + event.eventId()
            );

            return paymentRepository
                    .findByOrderId(event.orderId().intValue())
                    .orElse(null);
        }

        /*
         * Process the payment.
         *
         * processPayment() already contains protection against
         * creating multiple payments for the same order.
         */
        Payment payment =
                processPayment(
                        event.orderId().intValue(),
                        event.totalAmount()
                );

        /*
         * Mark the Kafka event as processed.
         *
         * This and the payment operation are inside the same
         * database transaction.
         */
        ProcessedEvent processedEvent =
                new ProcessedEvent(
                        event.eventId(),
                        LocalDateTime.now()
                );

        processedEventRepository.save(processedEvent);

        System.out.println(
                "Event processed successfully: "
                        + event.eventId()
        );

        return payment;
    }

    @Override
    public Optional<Payment> getPaymentByOrderId(Integer orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}