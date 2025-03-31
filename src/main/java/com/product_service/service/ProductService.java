package com.product_service.service;

import com.github.fge.jsonpatch.JsonPatch;
import com.product_service.exception.ProductNotFoundException;
import com.product_service.model.dto.ProductCreateDto;
import com.product_service.model.dto.ProductFilterDto;
import com.product_service.model.dto.ProductReadDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.util.UUID;

public interface ProductService {

    URI create(ProductCreateDto productDto);

    ProductReadDto findById(UUID id) throws ProductNotFoundException;

    Page<ProductReadDto> findBy(Pageable pageable, ProductFilterDto filter);

    ProductReadDto update(UUID id, JsonPatch patch);

    void delete(UUID id);

    void deleteBy(ProductFilterDto filter);
}