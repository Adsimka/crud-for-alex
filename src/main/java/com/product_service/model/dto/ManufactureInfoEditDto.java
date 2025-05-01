package com.product_service.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
public class ManufactureInfoEditDto {

        @Size(max = 50, message = "{product.manufacturer.name.size}")
        JsonNullable<String> manufacturerName = JsonNullable.undefined();

        @Size(max = 100, message = "{product.manufacturer.contact.size}")
        JsonNullable<String> manufacturerContact = JsonNullable.undefined();
}
