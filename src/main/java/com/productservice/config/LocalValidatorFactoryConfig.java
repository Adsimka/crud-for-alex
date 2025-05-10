package com.productservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@RequiredArgsConstructor
public class LocalValidatorFactoryConfig {

    private final MessageSource messageSource;

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        var validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.setValidationMessageSource(messageSource);

        return validatorFactory;
    }
}
