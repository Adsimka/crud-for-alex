package com.productservice.util.http;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@UtilityClass
public class UriBuilder {

    @NotNull
    public static URI buildCreateProductUri(@NotNull UUID id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
