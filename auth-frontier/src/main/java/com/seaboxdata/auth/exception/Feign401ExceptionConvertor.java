package com.seaboxdata.auth.exception;

import feign.FeignException;
import graphql.GraphQLError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author makaiyu
 * @date 2019/7/18 10:30
 */
@Slf4j
public class Feign401ExceptionConvertor {

    private String app;

    public Feign401ExceptionConvertor(String app) {
        this.app = app;
    }

    @ExceptionHandler(FeignException.class)
    public GraphQLError convExceptionToError(FeignException e) {
        try {
            if (e.status() == HttpStatus.UNAUTHORIZED.value()) {
                return new ServiceAccessError(app, "401", "用户未登录");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        throw e;
    }
}
