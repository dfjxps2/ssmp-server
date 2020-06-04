package com.seaboxdata.auth.exception;

import com.oembedler.moon.graphql.boot.GraphQLWebAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author makaiyu
 * @date 2019/7/18 10:19
 */
@Configuration
@ConditionalOnClass(name = "com.coxautodev.graphql.tools.GraphQLResolver")
@ConditionalOnProperty(prefix = "graphql.servlet", name = "enabled", havingValue = "true")
@AutoConfigureBefore(GraphQLWebAutoConfiguration.class)
public class AuthExceptionConf {

    @Value("${spring.application.name}")
    private String app;

    @Bean
    public AccessExceptionConvertor accessExceptionConvertor() {
        return new AccessExceptionConvertor(app);
    }

    @Bean
    public Feign401ExceptionConvertor feign401ExceptionConvertor() {
        return new Feign401ExceptionConvertor(app);
    }

}
