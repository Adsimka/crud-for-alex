package com.product_service.mapper;

import com.product_service.model.dto.ProductUpdateDto;
import com.product_service.model.entity.Product;
import com.product_service.util.json.JsonNullableUtils;
import org.springframework.stereotype.Component;


@Component
public class ProductUpdateApplier {

    public void apply(Product product, ProductUpdateDto dto) {
        JsonNullableUtils.apply(dto.getDescription(), product::setDescription);
        JsonNullableUtils.apply(dto.getName(), product::setName);
    }
}
