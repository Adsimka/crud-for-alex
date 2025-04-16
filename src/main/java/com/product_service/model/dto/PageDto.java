package com.product_service.model.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageDto<T> {

    private Integer number;

    private Integer size;

    private Long totalElements;

    private List<T> content;

    public PageDto(Page<T> page) {
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.content = page.getContent();
    }
}
