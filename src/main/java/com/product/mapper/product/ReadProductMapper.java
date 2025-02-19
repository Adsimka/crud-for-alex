package com.product.mapper.product;

import com.product.database.entity.Product;
import com.product.dto.ReadProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadProductMapper {

    ReadProductDto productToDto(Product product);
}
