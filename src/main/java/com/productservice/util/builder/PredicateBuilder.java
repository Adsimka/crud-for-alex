package com.productservice.util.builder;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class PredicateBuilder {

    public static class Builder<T> {

        private final List<T> conditions = new ArrayList<>();

        private T buildPredicate = null;

        public Builder<T> addString(String value, Function<String, T> function) {
            if (value != null && !value.trim().isEmpty()) {
                conditions.add(function.apply(value));
            }
            return this;
        }

        public Builder<T> buildAs(Function<List<T>, T> function) {
            buildPredicate = conditions.isEmpty() ? null : function.apply(conditions);
            return this;
        }

        public T orElseGet(Supplier<T> supplier) {
            return buildPredicate != null ? buildPredicate : supplier.get();
        }
    }

    public static <T> Builder<T> init() {
        return new Builder<>();
    }
}
