package com.product_service.exception;

public class CustomMessageException extends RuntimeException {

    public CustomMessageException(String message) {
        super(message);
    }
}