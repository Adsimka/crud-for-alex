package com.product_service.mapper;

import com.product_service.config.MappingConfig;
import com.product_service.model.dto.ManufactureInfoCreateDto;
import com.product_service.model.dto.ManufactureInfoEditDto;
import com.product_service.model.dto.ManufactureInfoReadDto;
import com.product_service.model.entity.embeddable.ManufacturerInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        config = MappingConfig.class,
        uses = { JsonNullableMapper.class }
)
public interface ManufacturerInfoMapper {

    ManufacturerInfo dtoToEntity(ManufactureInfoCreateDto dto);

    ManufactureInfoReadDto entityToDto(ManufacturerInfo manufacturerInfo);

    void update(ManufactureInfoEditDto dto, @MappingTarget ManufacturerInfo manufacturer);
}
