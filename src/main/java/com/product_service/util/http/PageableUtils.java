package com.product_service.util.http;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@UtilityClass
public class PageableUtils {

    public static Pageable sortIgnoreCase(Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().stream()
                .map(order -> Sort.Order
                        .by(order.getProperty())
                        .with(order.getDirection())
                        .ignoreCase()
                ).toList();

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(orders)
        );
    }
}
