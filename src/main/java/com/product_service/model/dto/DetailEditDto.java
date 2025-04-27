package com.product_service.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public class DetailEditDto {

        @Size(max = 100, message = "{product.detail.specifications.size}")
        JsonNullable<String> specifications = JsonNullable.undefined();

        @Size(max = 100, message = "{product.detail.supportInfo.size}")
        JsonNullable<String> supportInfo = JsonNullable.undefined();

        @Valid
        JsonNullable<ManufactureInfoEditDto> manufacture = JsonNullable.undefined();
}
