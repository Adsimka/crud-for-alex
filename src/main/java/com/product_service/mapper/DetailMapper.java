package com.product_service.mapper;

import com.product_service.config.MappingConfig;
import com.product_service.model.dto.DetailCreateDto;
import com.product_service.model.dto.DetailEditDto;
import com.product_service.model.dto.DetailReadDto;
import com.product_service.model.entity.embeddable.Detail;
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
