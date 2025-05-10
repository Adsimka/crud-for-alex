package com.productservice.util.specification.product;

import com.productservice.model.entity.Product;
import com.productservice.util.specification.SpecificationUtils;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static com.productservice.util.constants.Constants.DESCRIPTION;
import static com.productservice.util.constants.Constants.NAME;

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
