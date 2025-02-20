package com.product.service;

import com.product.dto.CreateProductDto;
import com.product.dto.EditProductDto;
import com.product.dto.ReadProductDto;
import com.product.error.exception.ProductNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ReadProductDto create(CreateProductDto productDto);

    ReadProductDto getProduct(Long id) throws ProductNotFoundException;

    List<ReadProductDto> getAllProducts(Pageable pageable);

    List<ReadProductDto> getProductByFilter(String name, Pageable pageable);

    ReadProductDto update(Long id, EditProductDto productDto);

    boolean delete(Long id);
}