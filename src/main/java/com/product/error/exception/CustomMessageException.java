package com.product.error.exception;

public class CustomMessageException extends RuntimeException {

    public CustomMessageException(String message) {
        super(message);
    }
}