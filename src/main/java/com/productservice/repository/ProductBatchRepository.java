package com.productservice.repository;

import com.productservice.model.dto.support.VariantRow;
import com.productservice.model.entity.Product;
import com.productservice.model.entity.embeddable.Detail;
import com.productservice.model.entity.embeddable.ManufacturerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${operation.batch.sql.update:500}")
    private int batchSize;

    public void insertProducts(List<Product> products) {
        if (products.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(
                """
                    INSERT INTO public.product (
                    id, name, description, specifications, support_info, manufacturer_name, manufacturer_contact
                    ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                products,
                batchSize,
                (statement, product) -> {
                    Detail detail = product.getDetail();
                    ManufacturerInfo manufacturer = detail == null ? null : detail.getManufacturerInfo();

                    statement.setObject(1, product.getId());
                    statement.setString(2, product.getName());
                    statement.setString(3, product.getDescription());
                    statement.setString(4, detail == null ? null : detail.getSpecifications());
                    statement.setString(5, detail == null ? null : detail.getSupportInfo());
                    statement.setString(6, manufacturer == null ? null : manufacturer.getManufacturerName());
                    statement.setString(7, manufacturer == null ? null : manufacturer.getManufacturerContact());
                }
        );
    }

    public void insertVariants(List<VariantRow> variants) {
        if (variants.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(
                """
                    INSERT INTO public.product_variant (product_id, code, name)
                    VALUES (?, ?, ?)
                """,
                variants,
                batchSize,
                (statement, variant) -> {
                    statement.setObject(1, variant.productId());
                    statement.setString(2, variant.code());
                    statement.setString(3, variant.name());
                }
        );
    }
}
