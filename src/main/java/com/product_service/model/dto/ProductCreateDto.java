package com.product_service.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductCreateDto(
        @Size(max = 20, message = "{product.name.size}")
        @NotBlank(message = "{product.name.notblank}")
        String name,
        @Size(min = 10, max = 50, message = "{product.description.size}")
        String description,
        @Valid
        DetailCreateDto detail
) {
}