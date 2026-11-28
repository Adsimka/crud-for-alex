package com.productservice.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductBatchUpdateDto(
        @NotNull
        @Size(max = 10000, message = "{product.batch.size}")
        List<@NotNull @Valid ProductBatchUpdateItemDto> products
) {
}
