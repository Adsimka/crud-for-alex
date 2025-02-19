package com.product.mapper.product;

import com.product.database.entity.Product;
import com.product.dto.EditProductDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UpdateProductMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product updateProductFromDto(EditProductDto productDto, @MappingTarget Product product);
}
