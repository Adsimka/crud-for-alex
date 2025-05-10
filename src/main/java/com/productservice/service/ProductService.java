package com.productservice.service;

import com.productservice.exception.ProductNotFoundException;
import com.productservice.model.dto.*;
import com.productservice.model.filter.ProductDeleteFilter;
import com.productservice.model.filter.ProductSearchFilter;
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