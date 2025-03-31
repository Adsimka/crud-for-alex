package com.product.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductCreateDto(
        @Size(max = 20, message = "Name cannot exceed 20 characters")
        @NotBlank(message = "Name cannot be blank")
        String name,
        @Size(min = 10, max = 50, message = "Description must be between 10 and 50 characters")
        String description) {
}