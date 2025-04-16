package com.product_service.util.json;

import lombok.experimental.UtilityClass;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.function.Consumer;

@UtilityClass
public class JsonNullableUtils {

    public <T> void apply(JsonNullable<T> value, Consumer<T> setter) {
        value.ifPresent(setter);
    }
}
