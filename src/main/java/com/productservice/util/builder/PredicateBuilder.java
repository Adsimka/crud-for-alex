package com.productservice.util.builder;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = PRIVATE)
public final class PredicateBuilder<P> {

    private final List<P> predicates = new ArrayList<>();

    public static <P> PredicateBuilder<P> init() {
        return new PredicateBuilder<>();
    }

    public <T> PredicateBuilder<P> add(T object, Function<T, P> condition) {
        if (nonNull(object)) {
            predicates.add(condition.apply(object));
        }
        return this;
    }

    public PredicateBuilder<P> addString(String string, Function<String, P> condition) {
        if (isNotBlank(string)) {
            predicates.add(condition.apply(string));
        }
        return this;
    }

    public <T> PredicateBuilder<P> addCollection(Collection<T> collection, Function<Collection<T>, P> condition) {
        if (isNotEmpty(collection)) {
            predicates.add(condition.apply(collection));
        }
        return this;
    }

    public <R> Optional<R> buildAs(Function<? super List<P>, R> condition) {
        return ofNullable(condition.apply(predicates));
    }
}
