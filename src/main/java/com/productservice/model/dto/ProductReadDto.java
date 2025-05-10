package com.productservice.model.dto;

public record ProductReadDto(
        String name,
        String description,
        DetailReadDto detail
) {
}
