package com.productservice.model.dto.support;

import java.util.UUID;

public record VariantUpdateRow(UUID id, UUID productId, String code, String name) {
}
