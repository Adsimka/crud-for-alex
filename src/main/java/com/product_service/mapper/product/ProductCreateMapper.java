package com.product_service.mapper.product;

import com.product_service.model.dto.ProductCreateDto;
import com.product_service.model.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductCreateMapper {

    Product dtoToProduct(ProductCreateDto product);
}
