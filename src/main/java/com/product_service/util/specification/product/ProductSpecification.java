package com.product_service.util.specification.product;

import com.product_service.model.entity.Product;
import com.product_service.util.specification.SpecificationUtils;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static com.product_service.util.constants.Constants.DESCRIPTION;
import static com.product_service.util.constants.Constants.NAME;

@UtilityClass
public class ProductSpecification {

    public static Specification<Product> nameLike(String name) {
        return SpecificationUtils.stringFieldContains(name, NAME);
    }

    public static Specification<Product> nameEqual(String name) {
        return SpecificationUtils.stringFieldEquals(name, NAME);
    }

    public static Specification<Product> descriptionLike(String description) {
        return SpecificationUtils.stringFieldContains(description, DESCRIPTION);
    }
}
