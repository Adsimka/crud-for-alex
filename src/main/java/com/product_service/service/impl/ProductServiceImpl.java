package com.product_service.service.impl;

import com.product_service.exception.ProductNotFoundException;
import com.product_service.mapper.ProductMapper;
import com.product_service.model.dto.*;
import com.product_service.model.entity.Product;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.product_service.util.http.PageableUtils.sortIgnoreCase;
import static com.product_service.util.constants.ErrorMessageConstants.PRODUCT_NOT_FOUND_MESSAGE;
import static com.product_service.util.specification.product.ProductSpecificationBuilder.specificationBuild;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Transactional
    @Override
    public UUID create(ProductCreateDto productDto) {
        Product product = productMapper.dtoToProduct(productDto);
        return productRepository.save(product).getId();
    }

    @Override
    public ProductReadDto findById(UUID id)  {
        return productRepository.findById(id)
                .map(productMapper::productToDto)
                .orElseThrow(
                        () -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id))
                );
    }

    @Override
    public PageDto<ProductReadDto> findBy(Pageable pageable, ProductFilter filter) {
        Page<ProductReadDto> page = productRepository.findAll(
                specificationBuild(filter),
                sortIgnoreCase(pageable)
        ).map(productMapper::productToDto);

        return new PageDto<>(page);
    }

    @Transactional
    @Override
    public ProductReadDto update(UUID id, ProductUpdateDto dto) {
        return productRepository.findById(id)
                .map(product -> {
                    productMapper.update(dto, product);
                    return productRepository.save(product);
                })
                .map(productMapper::productToDto)
                .orElseThrow(
                        () -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id))
                );
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByName(String name) {
        productRepository.deleteByName(name);
    }
}