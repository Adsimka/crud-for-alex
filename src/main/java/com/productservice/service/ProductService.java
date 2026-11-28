package com.productservice.service;

import com.productservice.exception.ProductNotFoundException;
import com.productservice.model.dto.PageDto;
import com.productservice.model.dto.ProductBatchCreateDto;
import com.productservice.model.dto.ProductBatchCreateResponse;
import com.productservice.model.dto.ProductBatchUpdateDto;
import com.productservice.model.dto.ProductBatchUpdateResponse;
import com.productservice.model.dto.ProductCreateDto;
import com.productservice.model.dto.ProductExistsResponse;
import com.productservice.model.dto.ProductReadDto;
import com.productservice.model.dto.ProductUpdateDto;
import com.productservice.model.filter.ProductDeleteFilter;
import com.productservice.model.filter.ProductSearchFilter;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    UUID create(ProductCreateDto productDto);

    ProductBatchCreateResponse createBatch(ProductBatchCreateDto dto);

    ProductBatchUpdateResponse updateBatch(ProductBatchUpdateDto dto);

    ProductReadDto findById(UUID id) throws ProductNotFoundException;

    PageDto<ProductReadDto> findBy(Pageable pageable, ProductSearchFilter filter);

    ProductExistsResponse existsById(UUID id);

    ProductReadDto update(UUID id, ProductUpdateDto dto);

    void delete(UUID id);

    void deleteBy(ProductDeleteFilter filter);
}