package com.productservice.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DetailEditDto {

        @Size(max = 100, message = "{product.detail.specifications.size}")
        JsonNullable<String> specifications = JsonNullable.undefined();

        @Size(max = 100, message = "{product.detail.supportInfo.size}")
        JsonNullable<String> supportInfo = JsonNullable.undefined();

        @Valid
        JsonNullable<ManufactureInfoEditDto> manufacturerInfo = JsonNullable.undefined();
}
