package com.productservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProductVariantUpdateDto {

    @NotNull
    private UUID id;

    @NotBlank(message = "{product.variant.code.notblank}")
    @Size(max = 20, message = "{product.variant.code.max}")
    private JsonNullable<String> code = JsonNullable.undefined();

    @NotBlank(message = "{product.variant.name.notblank}")
    @Size(max = 50, message = "{product.variant.name.max}")
    private JsonNullable<String> name = JsonNullable.undefined();
}
