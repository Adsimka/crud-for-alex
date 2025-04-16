package com.product_service.config;

import com.fasterxml.jackson.databind.Module;
import jakarta.validation.Validator;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class JacksonConfig {

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
}
