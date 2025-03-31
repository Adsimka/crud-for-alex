package com.product.service;

import com.github.fge.jsonpatch.JsonPatch;
import com.product.model.dto.ProductCreateDto;
import com.product.model.dto.ProductFilterDto;
import com.product.model.dto.ProductReadDto;
import com.product.exception.ProductNotFoundException;
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