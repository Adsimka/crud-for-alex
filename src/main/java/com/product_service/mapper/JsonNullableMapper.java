package com.product_service.mapper;

import com.product_service.config.MappingConfig;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(config = MappingConfig.class)
public abstract class JsonNullableMapper {

    public <T> T unwrap(JsonNullable<T> nullable) {
        return nullable == null ? null : nullable.orElse(null);
    }

    @Condition
    public <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }
}
