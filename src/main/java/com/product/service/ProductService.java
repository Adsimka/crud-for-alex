package com.product.service;

import com.product.dto.CreateProductDto;
import com.product.dto.EditProductDto;
import com.product.dto.ReadProductDto;
import com.product.error.exception.ProductNotFoundException;

import java.util.List;

public interface ProductService {

    ReadProductDto create(CreateProductDto productDto);

    ReadProductDto getProduct(Long id) throws ProductNotFoundException;

    List<ReadProductDto> getAllProducts();

    ReadProductDto update(Long id, EditProductDto productDto);

    boolean delete(Long id);
}