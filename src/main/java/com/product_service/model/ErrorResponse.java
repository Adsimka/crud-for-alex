package com.product_service.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private int statusCode;

    private String message;

    private LocalDateTime dateTime;
}
