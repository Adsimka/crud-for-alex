package com.product_service.specification.impl;

import com.product_service.model.dto.ProductFilter;
import com.product_service.model.entity.Product;
import com.product_service.specification.CustomSpecification;
import org.springframework.stereotype.Component;

import static com.product_service.util.specification.SpecificationUtils.descriptionContains;
import static com.product_service.util.specification.SpecificationUtils.nameContains;

@Component
public class ProductFindSpecification implements CustomSpecification<Product, ProductFilter> {

    public org.springframework.data.jpa.domain.Specification<Product> toSpecification(ProductFilter filter) {
        return org.springframework.data.jpa.domain.Specification.where(nameContains(filter.name()))
                .and(descriptionContains(filter.description()));
    }
}
