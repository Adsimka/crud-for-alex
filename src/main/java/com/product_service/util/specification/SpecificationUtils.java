package com.product_service.util.specification;

import com.product_service.model.entity.Product;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import static com.product_service.util.constants.Constants.PERCENT;

@UtilityClass
public class SpecificationUtils {

    public static Specification<Product> stringFieldContains(String fieldValue, String fieldName) {
        return (root, query, criteriaBuilder) -> StringUtils.hasText(fieldValue)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get(fieldName)), createSearchPattern(fieldValue))
                : null;
    }

    public static Specification<Product> stringFieldEquals(String fieldValue, String fieldName) {
        return (root, query, criteriaBuilder) -> StringUtils.hasText(fieldValue)
                ? criteriaBuilder.equal(root.get(fieldName), fieldValue)
                : null;
    }

    private static String createSearchPattern(String value) {
        return String.format("%s%s%s", PERCENT, value.toLowerCase(), PERCENT);
    }
}
