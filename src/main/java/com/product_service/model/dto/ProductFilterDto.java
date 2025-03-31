package com.product_service.model.dto;

import jakarta.validation.constraints.Size;

public record ProductFilterDto(
        @Size(max = 20, message = "Name cannot exceed 20 characters")
        String name,
        @Size(max = 50, message = "Description must be between 10 and 50 characters")
        String description) {
}
