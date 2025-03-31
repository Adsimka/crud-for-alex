package com.product.specification;

import com.product.model.dto.ProductFilterDto;
import com.product.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.product.util.constants.Constants.PERCENT;

@Component
public class ProductFindSpecification {

    public Specification<Product> toSpecification(ProductFilterDto filter) {
        return Specification.where(nameContains(filter.name()))
                .and(descriptionContains(filter.description()));
    }

    private Specification<Product> nameContains(String name) {
        return ((root, query, criteriaBuilder) -> StringUtils.hasText(name)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), createSearchPattern(name))
                : null);
    }

    private Specification<Product> descriptionContains(String description) {
        return ((root, query, criteriaBuilder) -> StringUtils.hasText(description)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), createSearchPattern(description))
                : null);
    }

    private String createSearchPattern(String value) {
        return String.format("%s%s%s", PERCENT, value.toLowerCase(), PERCENT);
    }
}
