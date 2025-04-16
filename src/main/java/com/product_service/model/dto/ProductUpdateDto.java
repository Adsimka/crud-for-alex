package com.product_service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
public class ProductUpdateDto {

    @NotNull(message = "product.name.notnull")
    @Size(max = 20, message = "product.name.size")
    private JsonNullable<String> name = JsonNullable.undefined();

    @Size(min = 10, max = 50, message = "product.name.size")
    private JsonNullable<String> description = JsonNullable.undefined();
}
