package com.product_service.util.specification;

import com.product_service.model.entity.Product;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import static com.product_service.util.constants.Constants.PERCENT;

@UtilityClass
public class SpecificationUtils {

    public static Specification<Product> nameContains(String name) {
        return ((root, query, criteriaBuilder) -> StringUtils.hasText(name)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), createSearchPattern(name))
                : null);
    }

    public static Specification<Product> descriptionContains(String description) {
        return ((root, query, criteriaBuilder) -> StringUtils.hasText(description)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), createSearchPattern(description))
                : null);
    }


    private String createSearchPattern(String value) {
        return String.format("%s%s%s", PERCENT, value.toLowerCase(), PERCENT);
    }
}
