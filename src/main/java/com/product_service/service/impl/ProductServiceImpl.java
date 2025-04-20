package com.product_service.service.impl;

import com.product_service.exception.ProductNotFoundException;
import com.product_service.mapper.product.ProductCreateMapper;
import com.product_service.mapper.product.ProductReadMapper;
import com.product_service.mapper.product.ProductUpdateMapper;
import com.product_service.model.dto.*;
import com.product_service.model.entity.Product;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductService;
import com.product_service.specification.impl.ProductFindSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.product_service.util.pageable.PageableUtils.sortIgnoreCase;
import static com.product_service.util.constants.ErrorMessageConstants.PRODUCT_NOT_FOUND_MESSAGE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductFindSpecification specification;
    
    private final ProductReadMapper productReadMapper;
    private final ProductCreateMapper productCreateMapper;
    private final ProductUpdateMapper productUpdateMapper;

    @Transactional
    @Override
    public UUID create(ProductCreateDto productDto) {
        Product product = productCreateMapper.dtoToProduct(productDto);
        return productRepository.save(product).getId();
    }

    @Override
    public ProductReadDto findById(UUID id)  {
        return productRepository.findById(id)
                .map(productReadMapper::productToDto)
                .orElseThrow(
                        () -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id))
                );
    }

    @Override
    public PageDto<ProductReadDto> findBy(Pageable pageable, ProductFilter filter) {
        Specification<Product> spec = specification.toSpecification(filter);
        Page<ProductReadDto> page = productRepository.findAll(spec, sortIgnoreCase(pageable))
                .map(productReadMapper::productToDto);

        return new PageDto<>(page);
    }

    @Transactional
    @Override
    public ProductReadDto update(UUID id, ProductUpdateDto dto) {
        return productRepository.findById(id)
                .map(product -> {
                    productUpdateMapper.update(dto, product);
                    return productRepository.save(product);
                })
                .map(productReadMapper::productToDto)
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