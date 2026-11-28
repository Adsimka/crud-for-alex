package com.productservice.exception;

import java.util.UUID;

public class ProductVariantNotFoundException extends RuntimeException {

    public ProductVariantNotFoundException(UUID productId, UUID variantId) {
        super("Variant %s was not found for product %s".formatted(variantId, productId));
    }
}
