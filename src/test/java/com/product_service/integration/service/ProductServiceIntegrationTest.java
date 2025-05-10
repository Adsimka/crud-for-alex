package com.product_service.integration.service;

import com.product_service.exception.ProductNotFoundException;
import com.product_service.model.dto.DetailEditDto;
import com.product_service.model.dto.ManufactureInfoEditDto;
import com.product_service.model.dto.ProductUpdateDto;
import com.product_service.model.entity.Product;
import com.product_service.model.entity.embeddable.Detail;
import com.product_service.model.entity.embeddable.ManufacturerInfo;
import com.product_service.repository.ProductRepository;
import com.product_service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Integration testing from ProductService")
class ProductServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("username")
            .withPassword("password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("TESTCONTAINERS_POSTGRES_URL", postgres::getJdbcUrl);
        registry.add("TESTCONTAINERS_POSTGRES_USERNAME", postgres::getUsername);
        registry.add("TESTCONTAINERS_POSTGRES_PASSWORD", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private UUID productId;

    @BeforeEach
    void setUp() {

        ManufacturerInfo manufacturerInfo = new ManufacturerInfo("Manuf", "Contact");
        Detail detail = new Detail("Spec", "Support", manufacturerInfo);

        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setDetail(detail);

        productId = productRepository.save(product).getId();
    }

    @Test
    @DisplayName("Update product with empty JSON - should not modify any fields")
    void updateWithEmptyJsonShouldNotUpdate() {

        ProductUpdateDto updateDto = new ProductUpdateDto();

        productService.update(productId, updateDto);

        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        assertEquals("Test Product", updatedProduct.getName());
        assertEquals("Test Description", updatedProduct.getDescription());
        assertEquals("Spec", updatedProduct.getDetail().getSpecifications());
        assertEquals("Manuf", updatedProduct.getDetail().getManufacturerInfo().getManufacturerName());
    }

    @Test
    @DisplayName("Update with different property not change")
    void updateWithDifferentPropertyNotChange() {

        ProductUpdateDto updateDto = new ProductUpdateDto(JsonNullable.of("Test Product"), null, null);

        productService.update(productId, updateDto);

        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        assertEquals("Test Product", updatedProduct.getName());
        assertEquals("Test Description", updatedProduct.getDescription());
    }

    @Test
    @DisplayName("Should update only name when provided and leave other fields unchanged")
    void updateWithDifferentPropertyShouldUpdate() {

        ProductUpdateDto updateDto = new ProductUpdateDto(JsonNullable.of("Updated Product"), null, null);

        productService.update(productId, updateDto);

        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals("Test Description", updatedProduct.getDescription());
    }

    @Test
    @DisplayName("Should set field to null when JsonNullable.of(null) is provided")
    void updateWithNullPropertyShouldSetNull() {

        ProductUpdateDto updateDto = new ProductUpdateDto(null, JsonNullable.of(null), null);

        productService.update(productId, updateDto);

        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        assertNull(updatedProduct.getDescription());
        assertEquals("Test Product", updatedProduct.getName());
        assertEquals("Manuf", updatedProduct.getDetail().getManufacturerInfo().getManufacturerName());
    }

    @Test
    @DisplayName("Should throw exception when not-null field is explicitly set to null")
    void updateWithInvalidNotNullFieldShouldThrowException() {

        ProductUpdateDto updateDto = new ProductUpdateDto(JsonNullable.of(null), null, null);

        assertThrows(Exception.class, () -> productService.update(productId, updateDto));
    }

    @Test
    @DisplayName("Should update nested manufacturer name and clear contact when null provided")
    void updateWithNestedObjectShouldUpdateNested() {

        ManufactureInfoEditDto manufEditDto = new ManufactureInfoEditDto(
                JsonNullable.of("Updated Manufacturer"),
                JsonNullable.of(null)
        );
        DetailEditDto detailEditDto = new DetailEditDto(null, null, JsonNullable.of(manufEditDto));
        ProductUpdateDto updateDto = new ProductUpdateDto(null, null, JsonNullable.of(detailEditDto));

        productService.update(productId, updateDto);

        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        String manufacturerName = updatedProduct.getDetail().getManufacturerInfo().getManufacturerName();
        assertEquals("Updated Manufacturer", manufacturerName);
        assertNull(updatedProduct.getDetail().getManufacturerInfo().getManufacturerContact());
        assertEquals("Spec", updatedProduct.getDetail().getSpecifications());
        assertEquals("Support", updatedProduct.getDetail().getSupportInfo());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existing product")
    void updateWithNonExistingProductShouldThrowException() {

        ProductUpdateDto updateDto = new ProductUpdateDto(null, null, null);
        UUID nonExistingId = UUID.randomUUID();

        assertThrows(ProductNotFoundException.class, () -> productService.update(nonExistingId, updateDto));
    }
}