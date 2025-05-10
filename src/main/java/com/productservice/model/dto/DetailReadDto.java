package com.productservice.model.dto;

public record DetailReadDto(
        String specifications,
        String supportInfo,
        ManufactureInfoReadDto manufacturerInfo
) {
}
