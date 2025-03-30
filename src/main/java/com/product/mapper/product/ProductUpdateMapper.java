package com.product.mapper.product;

import com.product.model.entity.Product;
import com.product.model.dto.ProductEditDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductUpdateMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product updateProductFromDto(ProductEditDto productDto, @MappingTarget Product product);
}
