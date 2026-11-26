package com.productservice.model.dto.support;

import java.util.UUID;

public record VariantRow(UUID productId, String code, String name) {
}
