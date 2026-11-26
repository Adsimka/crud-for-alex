package com.productservice.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.productservice.exception.ProductNotFoundException;
import com.productservice.model.dto.DetailEditDto;
import com.productservice.model.dto.ManufactureInfoEditDto;
import com.productservice.model.dto.ProductUpdateDto;
import com.productservice.model.entity.Product;
import com.productservice.model.entity.embeddable.Detail;
import com.productservice.model.entity.embeddable.ManufacturerInfo;
import com.productservice.repository.ProductRepository;
import com.productservice.service.ProductService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
    static void configureProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("TESTCONTAINERS_POSTGRES_URL", postgres::getJdbcUrl);
        registry.add("TESTCONTAINERS_POSTGRES_USERNAME", postgres::getUsername);
        registry.add("TESTCONTAINERS_POSTGRES_PASSWORD", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Test
    @DisplayName("Should accept an empty batch without creating rows")
    void createEmptyBatchShouldNotWrite() throws Exception {
        long productsBefore = productRepository.count();
        long variantsBefore = countVariants();

        postBatch(objectMapper.createArrayNode())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdProducts").value(0))
                .andExpect(jsonPath("$.createdVariants").value(0));

        assertEquals(productsBefore, productRepository.count());
        assertEquals(variantsBefore, countVariants());
    }

    @Test
    @DisplayName("Should create 10000 products with their associated variants")
    void createMaximumBatchShouldInsertProductsAndVariants() throws Exception {

        long productsBefore = productRepository.count();
        long variantsBefore = countVariants();

        ArrayNode products = objectMapper.createArrayNode();
        addProduct(products, "Batch-0");
        addProduct(products, "Batch-1").putArray("variants");

        for (int index = 2; index < 10000; index++) {
            String name = "Batch-" + index;
            ArrayNode variants = addProduct(products, name).putArray("variants");
            variants.addObject().put("code", name + "-default").put("name", "Default");
            variants.addObject().put("code", name + "-extra").put("name", "Extra");
        }

        postBatch(products)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdProducts").value(10000))
                .andExpect(jsonPath("$.createdVariants").value(19996));

        assertEquals(productsBefore + 10000, productRepository.count());
        assertEquals(variantsBefore + 19996, countVariants());
        Long linkedVariants = jdbcTemplate.queryForObject("""
                SELECT count(*)
                FROM public.product_variant variant
                JOIN public.product product ON product.id = variant.product_id
                WHERE product.name LIKE 'Batch-%'
                  AND variant.code IN (product.name || '-default', product.name || '-extra')
                """, Long.class);
        assertEquals(19996L, linkedVariants);
    }

    @Test
    @DisplayName("Should reject batches above the product limit and null nested variants")
    void createInvalidBatchShouldReturnBadRequest() throws Exception {
        long productsBefore = productRepository.count();
        long variantsBefore = countVariants();
        ArrayNode oversizedProducts = objectMapper.createArrayNode();
        for (int index = 0; index <= 10_000; index++) {
            addProduct(oversizedProducts, "Rejected-" + index);
        }
        ArrayNode invalidProducts = objectMapper.createArrayNode();
        addProduct(invalidProducts, "Rejected").putArray("variants").addNull();

        postBatch(oversizedProducts)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        postBatch(invalidProducts)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        assertEquals(productsBefore, productRepository.count());
        assertEquals(variantsBefore, countVariants());
    }

    private ResultActions postBatch(ArrayNode products) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.set("products", products);
        return mockMvc.perform(post("/api/v1/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)));
    }

    private ObjectNode addProduct(ArrayNode products, String name) {
        ObjectNode item = products.addObject();
        item.putObject("product").put("name", name);
        return item;
    }

    private long countVariants() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM public.product_variant", Long.class);
    }
}
