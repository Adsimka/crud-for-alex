package com.productservice.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "operation.batch.sql.update=500")
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Batch update of products and variants")
class ProductBatchUpdateIntegrationTest {

    private static final String BATCH_URL = "/api/v1/products/batch";
    private static final int PRODUCT_LIMIT = 10_000;

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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Updates 10000 products and their correctly linked variants")
    void updateMaximumBatchShouldUpdateProductsAndVariants() throws Exception {
        jdbcTemplate.update("""
                INSERT INTO public.product (name, description)
                SELECT 'Mass-before-' || number, 'Original description'
                FROM generate_series(1, ?) AS source(number)
                """, PRODUCT_LIMIT);
        jdbcTemplate.update("""
                INSERT INTO public.product_variant (product_id, code, name)
                SELECT id, replace(name, 'Mass-before-', 'old-'), 'Variant before'
                FROM public.product
                WHERE name LIKE 'Mass-before-%'
                """);

        List<LinkedRows> rows = jdbcTemplate.query("""
                SELECT product.id AS product_id, product.name, variant.id AS variant_id
                FROM public.product product
                JOIN public.product_variant variant ON variant.product_id = product.id
                WHERE product.name LIKE 'Mass-before-%'
                """, (row, number) -> new LinkedRows(
                row.getObject("product_id", UUID.class),
                row.getObject("variant_id", UUID.class),
                row.getString("name").substring("Mass-before-".length())
        ));
        assertEquals(PRODUCT_LIMIT, rows.size());

        ArrayNode products = objectMapper.createArrayNode();
        for (LinkedRows row : rows) {
            ObjectNode item = addItem(products, row.productId());
            item.putObject("product").put("name", "Mass-after-" + row.number());
            addVariant(item, row.variantId()).put("code", "new-" + row.number());
        }

        patchBatch(products)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedProducts").value(PRODUCT_LIMIT))
                .andExpect(jsonPath("$.updatedVariants").value(PRODUCT_LIMIT));

        Long matchingRows = jdbcTemplate.queryForObject("""
                SELECT count(*)
                FROM public.product product
                JOIN public.product_variant variant ON variant.product_id = product.id
                WHERE product.name LIKE 'Mass-after-%'
                  AND product.description = 'Original description'
                  AND variant.code = replace(product.name, 'Mass-after-', 'new-')
                  AND variant.name = 'Variant before'
                """, Long.class);
        assertEquals(PRODUCT_LIMIT, matchingRows);
    }

    @Test
    @DisplayName("Preserves omitted fields and clears explicitly null optional fields")
    void updateBatchShouldPreservePatchSemantics() throws Exception {
        UUID productId = seedProduct("Patch product");
        UUID variantId = seedVariant(productId, "patch-existing", "Variant before");
        ArrayNode products = objectMapper.createArrayNode();
        ObjectNode item = addItem(products, productId);
        ObjectNode product = item.putObject("product");
        product.putNull("description");
        ObjectNode detail = product.putObject("detail");
        detail.put("specifications", "Updated specifications");
        detail.putObject("manufacturerInfo").putNull("manufacturerContact");
        addVariant(item, variantId).put("name", "Variant after");

        patchBatch(products)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedProducts").value(1))
                .andExpect(jsonPath("$.updatedVariants").value(1));

        ProductValues expected = new ProductValues(
                "Patch product", null, "Updated specifications", "Original support", "Original maker", null
        );
        assertEquals(expected, loadProduct(productId));

        assertEquals(new VariantValues(productId, "patch-existing", "Variant after"), loadVariant(variantId));

        ArrayNode variantsOnly = objectMapper.createArrayNode();
        addVariant(addItem(variantsOnly, productId), variantId).put("code", "patch-new");

        patchBatch(variantsOnly)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedProducts").value(0))
                .andExpect(jsonPath("$.updatedVariants").value(1));

        assertEquals(expected, loadProduct(productId));
        assertEquals(new VariantValues(productId, "patch-new", "Variant after"), loadVariant(variantId));
    }

    @Test
    @DisplayName("Rolls back when a repeated variant ID is submitted for another product")
    void updateBatchShouldRollbackForVariantOfAnotherProduct() throws Exception {
        UUID firstProduct = seedProduct("Owner first");
        UUID secondProduct = seedProduct("Owner second");
        UUID firstVariant = seedVariant(firstProduct, "owner-valid", "Variant before");
        UUID foreignVariant = seedVariant(firstProduct, "owner-foreign", "Foreign variant");

        ProductValues originalFirst = loadProduct(firstProduct);
        ProductValues originalSecond = loadProduct(secondProduct);
        VariantValues originalVariant = loadVariant(firstVariant);
        VariantValues originalForeign = loadVariant(foreignVariant);

        ArrayNode products = objectMapper.createArrayNode();
        ObjectNode first = addItem(products, firstProduct);
        first.putObject("product").put("name", "Changed first");
        addVariant(first, firstVariant).put("name", "Changed variant");
        first.withArray("variants").addObject().put("id", foreignVariant.toString()).put("name", "Changed foreign");
        ObjectNode second = addItem(products, secondProduct);
        second.putObject("product").put("name", "Changed second");
        addVariant(second, foreignVariant).put("name", "Wrong owner change");

        patchBatch(products)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertEquals(originalFirst, loadProduct(firstProduct));
        assertEquals(originalSecond, loadProduct(secondProduct));
        assertEquals(originalVariant, loadVariant(firstVariant));
        assertEquals(originalForeign, loadVariant(foreignVariant));
    }

    @Test
    @DisplayName("Rolls back products and a completed JDBC batch when PostgreSQL rejects the next variant")
    void updateBatchShouldRollbackForDatabaseError() throws Exception {
        UUID productId = seedProduct("SQL rollback product");
        ProductValues originalProduct = loadProduct(productId);
        long variantPrefix = UUID.randomUUID().getMostSignificantBits();

        ArrayNode products = objectMapper.createArrayNode();
        ObjectNode item = addItem(products, productId);
        item.putObject("product").put("name", "SQL changed product");
        ArrayNode variants = item.putArray("variants");

        for (int number = 1; number <= 501; number++) {
            UUID variantId = new UUID(variantPrefix, number);
            seedVariant(variantId, productId, "rollback-" + number, "Original variant");
            String name = number == 501 ? "Invalid\0variant" : "Changed variant";
            variants.addObject().put("id", variantId.toString()).put("name", name);
        }

        patchBatch(products)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));

        assertEquals(originalProduct, loadProduct(productId));
        Long originalVariants = jdbcTemplate.queryForObject("""
                SELECT count(*) FROM public.product_variant
                WHERE product_id = ? AND name = 'Original variant'
                """, Long.class, productId);
        assertEquals(501L, originalVariants);
    }

    @Test
    @DisplayName("Applies the first entry for repeated product and variant IDs")
    void updateBatchShouldUseFirstEntryForDuplicateIdentifiers() throws Exception {
        UUID productId = seedProduct("Duplicate product");
        UUID variantId = seedVariant(productId, "duplicate", "Variant before");
        UUID otherVariantId = seedVariant(productId, "other", "Other variant");

        ArrayNode products = objectMapper.createArrayNode();
        ObjectNode first = addItem(products, productId);
        first.putObject("product").put("name", "Product first");
        ArrayNode variants = first.putArray("variants");
        variants.addObject().put("id", variantId.toString()).put("name", "Variant first");
        variants.addObject().put("id", variantId.toString()).put("name", "Variant second");

        ObjectNode duplicate = addItem(products, productId);
        duplicate.putObject("product").put("name", "Product second");
        addVariant(duplicate, otherVariantId).put("name", "Should be ignored");

        patchBatch(products)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedProducts").value(1))
                .andExpect(jsonPath("$.updatedVariants").value(1));

        assertEquals("Product first", loadProduct(productId).name());
        assertEquals(new VariantValues(productId, "duplicate", "Variant first"), loadVariant(variantId));
        assertEquals(new VariantValues(productId, "other", "Other variant"), loadVariant(otherVariantId));
    }

    @Test
    @DisplayName("Accepts an empty batch and rejects more than 10000 products")
    void updateBatchShouldEnforceProductLimit() throws Exception {
        patchBatch(objectMapper.createArrayNode())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedProducts").value(0))
                .andExpect(jsonPath("$.updatedVariants").value(0));

        ArrayNode products = objectMapper.createArrayNode();
        for (int index = 0; index <= PRODUCT_LIMIT; index++) {
            addItem(products, new UUID(0L, index)).putObject("product").put("name", "Too many products");
        }

        patchBatch(products)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Rejects a missing product without updating other products")
    void updateBatchShouldRejectMissingProduct() throws Exception {
        UUID productId = seedProduct("Existing product");
        ProductValues original = loadProduct(productId);
        ArrayNode products = objectMapper.createArrayNode();
        addItem(products, productId).putObject("product").put("name", "Changed product");
        addItem(products, UUID.randomUUID()).putObject("product").put("name", "Missing product");

        patchBatch(products)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertEquals(original, loadProduct(productId));
    }

    @Test
    @DisplayName("Rejects an explicit null for a required variant field")
    void updateBatchShouldRejectNullVariantName() throws Exception {
        UUID productId = seedProduct("Valid product");
        UUID variantId = seedVariant(productId, "variant", "Original variant");
        ProductValues original = loadProduct(productId);
        ArrayNode products = objectMapper.createArrayNode();
        ObjectNode item = addItem(products, productId);
        item.putObject("product").put("name", "Changed product");
        addVariant(item, variantId).putNull("name");

        patchBatch(products)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        assertEquals(original, loadProduct(productId));
        assertEquals(new VariantValues(productId, "variant", "Original variant"), loadVariant(variantId));
    }

    private ResultActions patchBatch(ArrayNode products) throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.set("products", products);
        return mockMvc.perform(patch(BATCH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)));
    }

    private ObjectNode addItem(ArrayNode products, UUID productId) {
        return products.addObject().put("id", productId.toString());
    }

    private ObjectNode addVariant(ObjectNode item, UUID variantId) {
        return item.putArray("variants").addObject().put("id", variantId.toString());
    }

    private UUID seedProduct(String name) {
        return jdbcTemplate.queryForObject("""
                INSERT INTO public.product (
                    name, description, specifications, support_info, manufacturer_name, manufacturer_contact
                ) VALUES (
                    ?, 'Original description', 'Original specifications', 'Original support',
                    'Original maker', 'Original contact'
                )
                RETURNING id
                """, UUID.class, name);
    }

    private UUID seedVariant(UUID productId, String code, String name) {
        UUID variantId = UUID.randomUUID();
        seedVariant(variantId, productId, code, name);
        return variantId;
    }

    private void seedVariant(UUID variantId, UUID productId, String code, String name) {
        jdbcTemplate.update("""
                INSERT INTO public.product_variant (id, product_id, code, name)
                VALUES (?, ?, ?, ?)
                """, variantId, productId, code, name);
    }

    private ProductValues loadProduct(UUID productId) {
        return jdbcTemplate.queryForObject("""
                SELECT name, description, specifications, support_info, manufacturer_name, manufacturer_contact
                FROM public.product
                WHERE id = ?
                """, (row, number) -> new ProductValues(
                row.getString("name"),
                row.getString("description"),
                row.getString("specifications"),
                row.getString("support_info"),
                row.getString("manufacturer_name"),
                row.getString("manufacturer_contact")
        ), productId);
    }

    private VariantValues loadVariant(UUID variantId) {
        return jdbcTemplate.queryForObject("""
                SELECT product_id, code, name
                FROM public.product_variant
                WHERE id = ?
                """, (row, number) -> new VariantValues(
                row.getObject("product_id", UUID.class), row.getString("code"), row.getString("name")
        ), variantId);
    }

    private record LinkedRows(UUID productId, UUID variantId, String number) {
    }

    private record ProductValues(
            String name, String description, String specifications, String supportInfo,
            String manufacturerName, String manufacturerContact
    ) {
    }

    private record VariantValues(UUID productId, String code, String name) {
    }
}
