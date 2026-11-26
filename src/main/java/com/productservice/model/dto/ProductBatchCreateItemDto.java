package com.productservice.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductBatchCreateItemDto(
        @NotNull @Valid ProductCreateDto product,
        List<@NotNull @Valid ProductVariantCreateDto> variants
) {

    public ProductBatchCreateItemDto {
        if (variants == null) {
            variants = List.of();
        }
    }
}
