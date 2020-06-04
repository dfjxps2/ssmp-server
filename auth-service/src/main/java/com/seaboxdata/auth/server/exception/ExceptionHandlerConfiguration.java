package com.seaboxdata.auth.server.exception;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionHandlerConfiguration {
    @Bean
    @ConditionalOnMissingBean(ServiceAccessExceptionHandler.class)
    public ServiceAccessExceptionHandler serviceAccessExceptionHandler() {
        return new ServiceAccessExceptionHandler();
    }
}
