package com.product_service.util.specification.product;

import com.product_service.model.dto.ProductFilter;
import com.product_service.model.entity.Product;
import com.product_service.util.specification.SpecificationUtils;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static com.product_service.util.constants.Constants.DESCRIPTION;
import static com.product_service.util.constants.Constants.NAME;

@UtilityClass
public class ProductSpecificationBuilder {

    public static Specification<Product> specificationBuild(ProductFilter filter) {
        return Specification.where(nameLike(filter.name()))
                .and(descriptionLike(filter.description()));
    }

    private static Specification<Product> nameLike(String name) {
        return SpecificationUtils.fieldContains(name, NAME);
    }

    private static Specification<Product> descriptionLike(String description) {
        return SpecificationUtils.fieldContains(description, DESCRIPTION);
    }
}
