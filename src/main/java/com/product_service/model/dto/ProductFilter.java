package com.product_service.model.dto;

import jakarta.validation.constraints.Size;

public record ProductFilter(
        @Size(max = 20, message = "product.name.size")
        String name,
        @Size(max = 50, message = "product.description.size")
        String description) {
}
