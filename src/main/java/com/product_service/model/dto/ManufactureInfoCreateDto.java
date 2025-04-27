package com.product_service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ManufactureInfoCreateDto(
        @NotNull(message = "{product.manufacturer.name.notnull}")
        @Size(max = 50, message = "{product.manufacturer.name.size}")
        String manufacturerName,
        @Size(max = 100, message = "{product.manufacturer.contact.size}")
        String manufacturerContact
) {
}
