package com.productservice.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record ProductVariantCreateDto(
        @Max(value = 20, message = "{product.variant.code.max}")
        @NotBlank(message = "{product.variant.code.notblank}")
        String code,
        @Max(value = 50, message = "{product.variant.name.max}")
        @NotBlank(message = "{product.variant.name.notblank}")
        String name
) {
}
