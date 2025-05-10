package com.productservice.model.dto;

import jakarta.validation.constraints.Size;

public record ManufactureInfoCreateDto(
        @Size(max = 50, message = "{product.manufacturer.name.size}")
        String manufacturerName,
        @Size(max = 100, message = "{product.manufacturer.contact.size}")
        String manufacturerContact
) {
}
