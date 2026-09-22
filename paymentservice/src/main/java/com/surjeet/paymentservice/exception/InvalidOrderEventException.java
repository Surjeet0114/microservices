package com.surjeet.paymentservice.exception;

public class InvalidOrderEventException extends RuntimeException {

    public InvalidOrderEventException(String message) {
        super(message);
    }
}