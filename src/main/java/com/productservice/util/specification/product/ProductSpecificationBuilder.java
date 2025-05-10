package com.productservice.util.specification.product;

import com.productservice.model.entity.Product;
import com.productservice.model.filter.ProductDeleteFilter;
import com.productservice.model.filter.ProductSearchFilter;
import com.productservice.util.builder.PredicateBuilder;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class ProductSpecificationBuilder {

    public static Specification<Product> buildSearchSpecification(ProductSearchFilter filter) {
        return PredicateBuilder.<Specification<Product>>init()
                .addString(filter.name(), ProductSpecification::nameLike)
                .addString(filter.description(), ProductSpecification::descriptionLike)
                .buildAs(Specification::allOf)
                .orElseGet(() -> Specification.where(null));
    }

    public static Specification<Product> buildDeleteSpecification(ProductDeleteFilter filter) {
        return PredicateBuilder.<Specification<Product>>init()
                .addString(filter.name(), ProductSpecification::nameEqual)
                .buildAs(Specification::allOf)
                .orElseGet(null);
    }
}
