package com.productservice.service.impl;

import com.productservice.exception.ProductNotFoundException;
import com.productservice.mapper.ProductMapper;
import com.productservice.model.dto.*;
import com.productservice.model.entity.Product;
import com.productservice.model.filter.ProductDeleteFilter;
import com.productservice.model.filter.ProductSearchFilter;
import com.productservice.repository.ProductRepository;
import com.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.productservice.util.specification.product.ProductSpecificationBuilder.buildDeleteSpecification;
import static com.productservice.util.specification.product.ProductSpecificationBuilder.buildSearchSpecification;
import static com.productservice.util.http.PageableUtils.sortIgnoreCase;
import static com.productservice.util.constants.ErrorMessageConstants.PRODUCT_NOT_FOUND_MESSAGE;

@Slf4j
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
        UUID id = productRepository.save(product).getId();

        log.info("Successfully created product with ID: {}", id);

        return id;
    }

    @Override
    public ProductReadDto findById(UUID id)  {
        log.info("Search product with ID: {}", id);
        return productRepository.findById(id)
                .map(productMapper::productToDto)
                .orElseThrow(
                        () -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id))
                );
    }

    @Override
    public PageDto<ProductReadDto> findBy(Pageable pageable, ProductSearchFilter filter) {
        log.info("Searching products with filter: {}", filter);
        return PageDto.fromPage(
                productRepository.findAll(buildSearchSpecification(filter), sortIgnoreCase(pageable))
                        .map(productMapper::productToDto)
        );
    }

    @Transactional
    @Override
    public ProductReadDto update(UUID id, ProductUpdateDto dto) {
        log.info("Updating product with ID: {}", id);
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
        log.info("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteBy(ProductDeleteFilter filter) {
        log.info("Deleting products with filter: {}", filter);
        productRepository.delete(buildDeleteSpecification(filter));
    }
}