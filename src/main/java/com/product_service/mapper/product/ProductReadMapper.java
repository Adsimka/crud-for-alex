package com.product_service.mapper.product;

import com.product_service.config.MappingConfig;
import com.product_service.model.dto.ProductReadDto;
import com.product_service.model.entity.Product;
import org.mapstruct.Mapper;

@Mapper(config = MappingConfig.class)
public interface ProductReadMapper {

    ProductReadDto productToDto(Product product);
}
