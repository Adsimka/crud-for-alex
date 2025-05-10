package com.product_service.service;

import com.product_service.exception.ProductNotFoundException;
import com.product_service.model.dto.*;
import com.product_service.model.filter.ProductDeleteFilter;
import com.product_service.model.filter.ProductSearchFilter;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    UUID create(ProductCreateDto productDto);

    ProductReadDto findById(UUID id) throws ProductNotFoundException;

    PageDto<ProductReadDto> findBy(Pageable pageable, ProductSearchFilter filter);

    ProductReadDto update(UUID id, ProductUpdateDto dto);

    void delete(UUID id);

    void deleteBy(ProductDeleteFilter filter);
}