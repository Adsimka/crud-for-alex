package com.product_service.util.http;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@UtilityClass
public class PageableUtils {

    @NotNull
    public static Pageable sortIgnoreCase(@NotNull Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().stream()
                .map(Sort.Order::ignoreCase)
                .toList();

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(orders)
        );
    }
}
