package com.product_service.model.dto;

public record DetailReadDto(
        String specifications,
        String supportInfo,
        ManufactureInfoReadDto manufacture
) {
}
