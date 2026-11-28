package com.productservice.model.dto;

import java.util.UUID;

public record ProductVariantReadDto(UUID id, String code, String name) {
}
