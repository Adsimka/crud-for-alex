package com.productservice.util.specification;

import com.productservice.model.entity.Product;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import static com.productservice.util.constants.Constants.PERCENT;

@UtilityClass
public class SpecificationUtils {

    public static Specification<Product> stringFieldContains(String fieldValue, @NonNull String fieldName) {
        return (root, query, criteriaBuilder) -> StringUtils.hasText(fieldValue)
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get(fieldName)), createSearchPattern(fieldValue))
                : null;
    }

    public static Specification<Product> stringFieldEquals(String fieldValue, @NonNull String fieldName) {
        return (root, query, criteriaBuilder) -> StringUtils.hasText(fieldValue)
                ? criteriaBuilder.equal(root.get(fieldName), fieldValue)
                : null;
    }

    @NonNull
    private static String createSearchPattern(@NonNull String value) {
        return String.format("%s%s%s", PERCENT, value.toLowerCase(), PERCENT);
    }
}
