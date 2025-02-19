package com.product.service;

import com.product.database.repository.ProductRepository;
import com.product.dto.CreateProductDto;
import com.product.dto.EditProductDto;
import com.product.dto.ReadProductDto;
import com.product.error.exception.ProductNotFoundException;
import com.product.mapper.product.CreateProductMapper;
import com.product.mapper.product.ReadProductMapper;
import com.product.mapper.product.UpdateProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.product.error.constants.ErrorMessageConstants.PRODUCT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    
    private final ReadProductMapper readProductMapper; 
    private final CreateProductMapper createProductMapper;
    private final UpdateProductMapper updateProductMapper;

    @Transactional
    public ReadProductDto create(CreateProductDto productDto) {
        return Optional.of(productDto)
                .map(createProductMapper::dtoToProduct)
                .map(productRepository::saveAndFlush)
                .map(readProductMapper::productToDto)
                .orElseThrow();
    }

    public ReadProductDto getProduct(Long id)  {
        return productRepository.findById(id)
                .map(readProductMapper::productToDto)
                .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND, id)));
    }

    public List<ReadProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(readProductMapper::productToDto)
                .toList();
    }

    @Transactional
    public ReadProductDto update(Long id, EditProductDto productDto) {
        return productRepository.findById(id)
                .map(product -> updateProductMapper.updateProductFromDto(productDto, product))
                .map(productRepository::saveAndFlush)
                .map(readProductMapper::productToDto)
                .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND, id)));
    }

    @Transactional
    public boolean delete(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.deleteById(id);
                    productRepository.flush();

                    return true;
                })
                .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND, id)));
    }
}