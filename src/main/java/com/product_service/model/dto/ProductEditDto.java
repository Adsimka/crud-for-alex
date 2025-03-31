package com.product_service.model.dto;

import jakarta.validation.constraints.Size;

public record ProductEditDto(
        @Size(max = 20, message = "Name cannot exceed 20 characters")
        String name,
        @Size(max = 50, message = "Description cannot exceed 20 characters")
        String description) {
}