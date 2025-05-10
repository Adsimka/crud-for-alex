package com.productservice.mapper;

import com.productservice.config.MappingConfig;
import com.productservice.model.dto.ManufactureInfoCreateDto;
import com.productservice.model.dto.ManufactureInfoEditDto;
import com.productservice.model.dto.ManufactureInfoReadDto;
import com.productservice.model.entity.embeddable.ManufacturerInfo;
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
