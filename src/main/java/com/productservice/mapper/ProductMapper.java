package com.productservice.mapper;

import com.productservice.config.MappingConfig;
import com.productservice.model.dto.ProductCreateDto;
import com.productservice.model.dto.ProductReadDto;
import com.productservice.model.dto.ProductUpdateDto;
import com.productservice.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        config = MappingConfig.class,
        uses = { JsonNullableMapper.class, DetailMapper.class }
)
public interface ProductMapper {

    Product dtoToProduct(ProductCreateDto product);

    ProductReadDto productToDto(Product product);

    void update(ProductUpdateDto dto, @MappingTarget Product product);
}
