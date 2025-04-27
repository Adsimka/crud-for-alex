package com.product_service.model.dto;

public record ProductReadDto(
        String name,
        String description,
        DetailReadDto detail
) {
}
