package com.product.service.impl;

import com.product.mapper.product.ProductCreateMapper;
import com.product.model.dto.ProductCreateDto;
import com.product.model.dto.ProductFilterDto;
import com.product.model.dto.ProductReadDto;
import com.product.model.entity.Product;
import com.product.repository.ProductRepository;
import com.product.model.dto.ProductEditDto;
import com.product.exception.CustomMessageException;
import com.product.exception.ProductNotFoundException;
import com.product.mapper.product.ProductReadMapper;
import com.product.mapper.product.ProductUpdateMapper;
import com.product.service.ProductService;
import com.product.specification.ProductFindSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static com.product.util.constants.ErrorMessageConstants.PRODUCT_NOT_FOUND_MESSAGE;
import static com.product.util.constants.ErrorMessageConstants.PRODUCT_CREATION_ERROR;
import static com.product.util.http.UriBuilder.buildCreateProductUri;

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
    public URI create(ProductCreateDto productDto) {
        UUID id = Optional.of(productDto)
                .map(productCreateMapper::dtoToProduct)
                .map(productRepository::saveAndFlush)
                .map(Product::getId)
                .orElseThrow(() -> new CustomMessageException(PRODUCT_CREATION_ERROR));

        return buildCreateProductUri(id);
    }

    @Override
    public ProductReadDto findById(UUID id)  {
        return productRepository.findById(id)
                .map(productReadMapper::productToDto)
                .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id)));
    }

    @Override
    public Page<ProductReadDto> findBy(Pageable pageable, ProductFilterDto filter) {
        Specification<Product> spec = specification.toSpecification(filter);
        return productRepository.findAll(spec, pageable)
                .map(productReadMapper::productToDto);
    }

    @Transactional
    @Override
    public ProductReadDto update(UUID id, ProductEditDto productDto) {
        return productRepository.findById(id)
                .map(product -> productUpdateMapper.updateProductFromDto(productDto, product))
                .map(productRepository::saveAndFlush)
                .map(productReadMapper::productToDto)
                .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id)));
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        productRepository.findById(id)
                .ifPresent(product -> {
                    productRepository.delete(product);
                    productRepository.flush();
                });
    }

    @Transactional
    @Override
    public void deleteBy(ProductFilterDto filter) {
        Specification<Product> spec = specification.toSpecification(filter);
        productRepository.delete(spec);
    }
}