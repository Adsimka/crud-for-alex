package com.product.mapper.product;

import com.product.database.entity.Product;
import com.product.dto.CreateProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreateProductMapper {

    Product dtoToProduct(CreateProductDto product);
}
