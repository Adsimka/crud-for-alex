package com.product_service.util.specification;

import com.product_service.model.entity.Product;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import static com.product_service.util.constants.Constants.PERCENT;

@UtilityClass
public class SpecificationUtils {

    public static Specification<Product> fieldContains(String field, String fieldName) {
        return ((root, query, criteriaBuilder) -> StringUtils.hasText(field)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get(fieldName)), createSearchPattern(field))
                : null);
    }

    private String createSearchPattern(String value) {
        return String.format("%s%s%s", PERCENT, value.toLowerCase(), PERCENT);
    }
}
