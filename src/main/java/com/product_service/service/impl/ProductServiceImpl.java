package com.product_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.product_service.exception.CustomMessageException;
import com.product_service.exception.ProductNotFoundException;
import com.product_service.mapper.product.ProductCreateMapper;
import com.product_service.mapper.product.ProductReadMapper;
import com.product_service.model.dto.ProductCreateDto;
import com.product_service.model.dto.ProductFilterDto;
import com.product_service.model.dto.ProductReadDto;
import com.product_service.model.entity.Product;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductService;
import com.product_service.specification.ProductFindSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static com.product_service.util.constants.ErrorMessageConstants.PRODUCT_CREATION_ERROR;
import static com.product_service.util.constants.ErrorMessageConstants.PRODUCT_NOT_FOUND_MESSAGE;
import static com.product_service.util.http.UriBuilder.buildCreateProductUri;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductFindSpecification specification;
    private final ObjectMapper mapper;
    
    private final ProductReadMapper productReadMapper;
    private final ProductCreateMapper productCreateMapper;

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
                .orElseThrow(
                        () -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id))
                );
    }

    @Override
    public Page<ProductReadDto> findBy(Pageable pageable, ProductFilterDto filter) {
        Specification<Product> spec = specification.toSpecification(filter);
        return productRepository.findAll(spec, pageable)
                .map(productReadMapper::productToDto);
    }

    @Transactional
    @Override
    public ProductReadDto update(UUID id, JsonPatch patch) {
        return productRepository.findById(id)
                .map(product -> applyPatch(product, patch))
                .map(productRepository::saveAndFlush)
                .map(productReadMapper::productToDto)
                .orElseThrow(
                        () -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, id))
                );
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

    private Product applyPatch(Product product, JsonPatch patch) {
        try {
            JsonNode patched = patch.apply(mapper.convertValue(product, JsonNode.class));
            return mapper.treeToValue(patched, Product.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}