package com.product_service.model.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        Integer number,
        Integer size,
        Long totalElements
) {

    public static <T> PageDto<T> fromPage(Page<T> page) {
        return new PageDto<T>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}
