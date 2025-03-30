package com.product.mapper.product;

import com.product.model.entity.Product;
import com.product.model.dto.ProductReadDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductReadMapper {

    ProductReadDto productToDto(Product product);
}
