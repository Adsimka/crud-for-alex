package com.productservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductVariantCreateDto(
        @Size(max = 20, message = "{product.variant.code.max}")
        @NotBlank(message = "{product.variant.code.notblank}")
        String code,
        @Size(max = 50, message = "{product.variant.name.max}")
        @NotBlank(message = "{product.variant.name.notblank}")
        String name
) {
}
