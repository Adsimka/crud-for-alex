package com.product_service.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
public class ProductUpdateDto {

    @NotBlank(message = "{product.name.notblank}")
    @Size(max = 20, message = "{product.name.size}")
    private JsonNullable<String> name = JsonNullable.undefined();

    @Size(min = 10, max = 50, message = "{product.name.size}")
    private JsonNullable<String> description = JsonNullable.undefined();

    @Valid
    private JsonNullable<DetailEditDto> detail = JsonNullable.undefined();
}
