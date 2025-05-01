package com.product_service.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record DetailCreateDto(
        @Size(max = 100, message = "{product.detail.specifications.size}")
        String specifications,
        @Size(max = 100, message = "{product.detail.supportInfo.size}")
        String supportInfo,
        @Valid
        ManufactureInfoCreateDto manufacturerInfo
) {
}