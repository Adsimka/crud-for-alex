package com.productservice.model.filter;

import jakarta.validation.constraints.Size;

public record ProductSearchFilter(
        @Size(max = 20, message = "{product.name.size}")
        String name,
        @Size(max = 50, message = "{product.description.size}")
        String description) {
}
