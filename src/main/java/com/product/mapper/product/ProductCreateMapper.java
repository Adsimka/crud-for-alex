package com.product.mapper.product;

import com.product.model.dto.ProductCreateDto;
import com.product.model.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductCreateMapper {

    Product dtoToProduct(ProductCreateDto product);
}
