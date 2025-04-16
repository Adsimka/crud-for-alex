package com.product_service.specification;

import com.product_service.model.dto.ProductFilter;
import com.product_service.specification.impl.ProductFindSpecification;
import com.product_service.specification.type.SpecificationType;

import static com.product_service.specification.type.SpecificationType.PRODUCT_SPECIFICATION;

public class SpecificationBuilder<T extends CustomSpecification, P> {

//    public T getSpecification(SpecificationType type, P filter) {
//
//        if (PRODUCT_SPECIFICATION.equals(type)) {
//            return new ProductFindSpecification()
//                    .toSpecification(filter);
//        }
//    }
}
