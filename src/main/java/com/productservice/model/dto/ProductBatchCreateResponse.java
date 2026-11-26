package com.productservice.model.dto;

public record ProductBatchCreateResponse(int createdProducts, int createdVariants) {

    public static ProductBatchCreateResponse emptyResponse() {
        return new ProductBatchCreateResponse(0, 0);
    }
}
