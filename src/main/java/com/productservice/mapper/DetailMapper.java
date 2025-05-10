package com.productservice.mapper;

import com.productservice.config.MappingConfig;
import com.productservice.model.dto.DetailCreateDto;
import com.productservice.model.dto.DetailEditDto;
import com.productservice.model.dto.DetailReadDto;
import com.productservice.model.entity.embeddable.Detail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        config = MappingConfig.class,
        uses = { JsonNullableMapper.class, ManufacturerInfoMapper.class }
)
public interface DetailMapper {

    Detail dtoToEntity(DetailCreateDto dto);

    DetailReadDto entityToDto(Detail detail);

    void update(DetailEditDto dto, @MappingTarget Detail detail);
}
