package com.productservice.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ProductBatchUpdateItemDto(
        @NotNull UUID id,
        @Valid ProductUpdateDto product,
        List<@NotNull @Valid ProductVariantUpdateDto> variants
) {

    public ProductBatchUpdateItemDto {
        if (variants == null) {
            variants = List.of();
        }
    }
}
