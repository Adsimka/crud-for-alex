package com.product_service.mapper;

import com.product_service.config.MappingConfig;
import com.product_service.model.dto.ProductCreateDto;
import com.product_service.model.dto.ProductReadDto;
import com.product_service.model.dto.ProductUpdateDto;
import com.product_service.model.entity.Product;
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
