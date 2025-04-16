package com.product_service.specification;

public interface CustomSpecification<T, P> {

    public org.springframework.data.jpa.domain.Specification<T> toSpecification(P filter);
}
