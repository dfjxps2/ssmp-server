package com.seaboxdata.auth.exception;

import graphql.GraphQLError;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;

/**
 * @author makaiyu
 * @date 2019/7/18 10:30
 */
public class AccessExceptionConvertor {

    private String app;

    public AccessExceptionConvertor(String app) {
        this.app = app;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public GraphQLError convExceptionToError(AccessDeniedException e) {
        try {
            LinkedHashMap map = (LinkedHashMap) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            return new ServiceAccessError(app, "403", "用户无权限");
        } catch (Exception ex) {
            return new ServiceAccessError(app, "401", "用户未登录");
        }
    }

}
