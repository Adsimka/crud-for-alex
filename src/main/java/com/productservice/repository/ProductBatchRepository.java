package com.productservice.repository;

import com.productservice.exception.ProductNotFoundException;
import com.productservice.exception.ProductVariantNotFoundException;
import com.productservice.model.dto.support.VariantRow;
import com.productservice.model.dto.support.VariantUpdateRow;
import com.productservice.model.entity.Product;
import com.productservice.model.entity.embeddable.Detail;
import com.productservice.model.entity.embeddable.ManufacturerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static com.productservice.util.constants.ErrorMessageConstants.PRODUCT_NOT_FOUND_MESSAGE;

@Repository
@RequiredArgsConstructor
public class ProductBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Value("${operation.batch.sql.update:500}")
    private int batchSize;

    public void insertProducts(List<Product> products) {
        if (products.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate("""
                INSERT INTO public.product (
                    name, description, specifications, support_info, manufacturer_name, manufacturer_contact, id
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """, products, batchSize, this::bindProduct);
    }

    public void insertVariants(List<VariantRow> variants) {
        if (variants.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate("""
                INSERT INTO public.product_variant (product_id, code, name)
                VALUES (?, ?, ?)
                """, variants, batchSize, (statement, variant) -> {
            statement.setObject(1, variant.productId());
            statement.setString(2, variant.code());
            statement.setString(3, variant.name());
        });
    }

    public List<Product> findProductsForUpdate(Collection<UUID> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return namedParameterJdbcTemplate.query("""
                SELECT id, name, description, specifications, support_info, manufacturer_name, manufacturer_contact
                FROM public.product
                WHERE id IN (:ids)
                ORDER BY id
                FOR NO KEY UPDATE
                """,
                Map.of("ids", ids),
                (row, index) -> readProduct(row)
        );
    }

    public void updateProducts(List<Product> products) {
        if (products.isEmpty()) {
            return;
        }
        int[][] result = jdbcTemplate.batchUpdate("""
                UPDATE public.product
                SET name = ?, description = ?, specifications = ?, support_info = ?,
                    manufacturer_name = ?, manufacturer_contact = ?
                WHERE id = ?
                """, products,
                batchSize,
                this::bindProduct
        );

        requireUpdatedRows(
                result,
                products,
                product -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_MESSAGE, product.getId()))
        );
    }

    public void updateVariants(List<VariantUpdateRow> variants) {
        if (variants.isEmpty()) {
            return;
        }
        int[][] result = jdbcTemplate.batchUpdate("""
                UPDATE public.product_variant
                SET code = COALESCE(?, code), name = COALESCE(?, name)
                WHERE id = ? AND product_id = ?
                """,
                variants,
                batchSize,
                (statement, variant) -> {
                    statement.setString(1, variant.code());
                    statement.setString(2, variant.name());
                    statement.setObject(3, variant.id());
                    statement.setObject(4, variant.productId());
                }
        );
        requireUpdatedRows(
                result,
                variants,
                variant -> new ProductVariantNotFoundException(variant.productId(), variant.id())
        );
    }

    private Product readProduct(ResultSet row) throws SQLException {

        String manufacturerName = row.getString("manufacturer_name");
        String manufacturerContact = row.getString("manufacturer_contact");
        ManufacturerInfo manufacturer = manufacturerName == null
                && manufacturerContact == null ? null : new ManufacturerInfo(manufacturerName, manufacturerContact);

        String specifications = row.getString("specifications");
        String supportInfo = row.getString("support_info");
        Detail detail = specifications == null
                && supportInfo == null
                && manufacturer == null ? null : new Detail(specifications, supportInfo, manufacturer);

        return new Product(
                row.getObject("id", UUID.class),
                row.getString("name"),
                row.getString("description"),
                detail
        );
    }

    private void bindProduct(PreparedStatement statement, Product product) throws SQLException {

        Detail detail = product.getDetail();
        ManufacturerInfo manufacturer = detail == null ? null : detail.getManufacturerInfo();

        statement.setString(1, product.getName());
        statement.setString(2, product.getDescription());
        statement.setString(3, detail == null ? null : detail.getSpecifications());
        statement.setString(4, detail == null ? null : detail.getSupportInfo());
        statement.setString(5, manufacturer == null ? null : manufacturer.getManufacturerName());
        statement.setString(6, manufacturer == null ? null : manufacturer.getManufacturerContact());
        statement.setObject(7, product.getId());
    }

    private <T> void requireUpdatedRows(int[][] result, List<T> rows, Function<T, RuntimeException> missingRow) {
        int[] counts = Arrays.stream(result).flatMapToInt(Arrays::stream).toArray();
        if (counts.length != rows.size()) {
            throw new DataIntegrityViolationException("JDBC returned an incomplete batch update result");
        }
        for (int index = 0; index < counts.length; index++) {
            if (counts[index] == 0) {
                throw missingRow.apply(rows.get(index));
            }
            if (counts[index] != 1) {
                throw new DataIntegrityViolationException("JDBC returned an unexpected batch update count");
            }
        }
    }
}
