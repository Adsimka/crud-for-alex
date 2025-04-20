package com.product_service.mapper.product;

import com.product_service.config.MappingConfig;
import com.product_service.mapper.JsonNullableMapper;
import com.product_service.model.dto.ProductUpdateDto;
import com.product_service.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        config = MappingConfig.class,
        uses = { JsonNullableMapper.class }
)
public interface ProductUpdateMapper {

    void update(ProductUpdateDto dto, @MappingTarget Product product);
}
