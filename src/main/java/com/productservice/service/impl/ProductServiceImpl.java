package com.productservice.service.impl;

import com.productservice.exception.ProductNotFoundException;
import com.productservice.mapper.ProductMapper;
import com.productservice.model.dto.PageDto;
import com.productservice.model.dto.ProductBatchCreateDto;
import com.productservice.model.dto.ProductBatchCreateItemDto;
import com.productservice.model.dto.ProductBatchCreateResponse;
import com.productservice.model.dto.ProductBatchUpdateDto;
import com.productservice.model.dto.ProductBatchUpdateItemDto;
import com.productservice.model.dto.ProductBatchUpdateResponse;
import com.productservice.model.dto.ProductCreateDto;
import com.productservice.model.dto.ProductExistsResponse;
import com.productservice.model.dto.ProductReadDto;
import com.productservice.model.dto.ProductUpdateDto;
import com.productservice.model.dto.ProductVariantCreateDto;
import com.productservice.model.dto.ProductVariantUpdateDto;
import com.productservice.model.dto.support.VariantRow;
import com.productservice.model.dto.support.VariantUpdateRow;
import com.productservice.model.entity.Product;
import com.productservice.model.filter.ProductDeleteFilter;
import com.productservice.model.filter.ProductSearchFilter;
import com.productservice.repository.ProductBatchRepository;
import com.productservice.repository.ProductRepository;
import com.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.productservice.util.constants.ErrorMessageConstants.PRODUCT_NOT_FOUND_MESSAGE;
import static com.productservice.util.http.PageableUtils.sortIgnoreCase;
import static com.productservice.util.specification.product.ProductSpecificationBuilder.buildDeleteSpecification;
import static com.productservice.util.specification.product.ProductSpecificationBuilder.buildSearchSpecification;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductBatchRepository productBatchRepository;

    private final ProductMapper productMapper;

    @Transactional
    @Override
    public UUID create(ProductCreateDto productDto) {
        Product product = productMapper.dtoToProduct(productDto);
        UUID id = productRepository.save(product).getId();

        log.info("Successfully created product with ID: {}", id);

        return id;
    }

    @Transactional
    @Override
    public ProductBatchCreateResponse createBatch(ProductBatchCreateDto dto) {

        if (CollectionUtils.isEmpty(dto.products())) {
            log.info("Empty products list");
            return ProductBatchCreateResponse.emptyResponse();
        }
        List<Product> products = new ArrayList<>(dto.products().size());
        List<VariantRow> variants = new ArrayList<>();

        createBatchProcess(dto, products, variants);

        return new ProductBatchCreateResponse(products.size(), variants.size());
    }

    @Transactional
    @Override
    public ProductBatchUpdateResponse updateBatch(ProductBatchUpdateDto dto) {

        List<VariantUpdateRow> variants = new ArrayList<>();
        List<Product> products = updateBatchProcess(dto, variants);

        return new ProductBatchUpdateResponse(products.size(), variants.size());
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

    @Override
    public ProductExistsResponse existsById(UUID id) {
        return productMapper.toExistsResponse(productRepository.existsById(id));
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

    private void createBatchProcess(ProductBatchCreateDto dto, List<Product> products, List<VariantRow> variants) {

        for (ProductBatchCreateItemDto item : dto.products()) {
            Product product = productMapper.dtoToProduct(item.product());
            UUID productId = UUID.randomUUID();
            product.setId(productId);
            products.add(product);

            for (ProductVariantCreateDto variant : item.variants()) {
                variants.add(new VariantRow(productId, variant.code(), variant.name()));
            }
        }

        productBatchRepository.insertProducts(products);
        productBatchRepository.insertVariants(variants);

        log.info("Saved {} products, {} variants", products.size(), variants.size());
    }

    private List<Product> updateBatchProcess(ProductBatchUpdateDto dto, List<VariantUpdateRow> variants) {

        Map<UUID, ProductBatchUpdateItemDto> items = new LinkedHashMap<>();

        for (ProductBatchUpdateItemDto item : dto.products()) {
            if (items.putIfAbsent(item.id(), item) != null) {
                log.info("Duplicate product id: {}", item.id());
                continue;
            }
            Set<UUID> variantIds = new HashSet<>();
            for (ProductVariantUpdateDto variant : item.variants()) {
                if (!variantIds.add(variant.getId())) {
                    log.info("Duplicate variant id: {} for product {}", variant.getId(), item.id());
                    continue;
                }
                variants.add(new VariantUpdateRow(
                        variant.getId(), item.id(), variant.getCode().orElse(null), variant.getName().orElse(null)
                ));
            }
        }

        List<Product> products = prepareProductUpdates(items);
        variants.sort(Comparator.comparing(VariantUpdateRow::id));

        productBatchRepository.updateProducts(products);
        productBatchRepository.updateVariants(variants);

        return products;
    }

    private List<Product> prepareProductUpdates(Map<UUID, ProductBatchUpdateItemDto> items) {

        List<Product> products = new ArrayList<>(items.size());

        for (Product product : productBatchRepository.findProductsForUpdate(items.keySet())) {
            ProductBatchUpdateItemDto item = items.remove(product.getId());
            if (item.product() != null) {
                productMapper.update(item.product(), product);
                products.add(product);
            }
        }
        if (!CollectionUtils.isEmpty(items)) {
            UUID missingId = items.keySet().iterator().next();
            throw new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, missingId));
        }
        return products;
    }
}