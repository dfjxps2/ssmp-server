package com.seaboxdata.auth.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * @author makaiyu
 * @date 2019/7/18 11:13
 */
@ControllerAdvice("com.seaboxdata")
@Slf4j
public class ServiceAccessExceptionHandler {

    @Value("${spring.application.name}")
    private String app;

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public FeignClientError handleRuntimeException(AccessDeniedException e, HttpServletRequest request, HttpServletResponse response) {
        FeignClientError error = new FeignClientError();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();
        if (authorities.size() > NumberUtils.INTEGER_ONE) {
            response.setStatus(403);
            error = new FeignClientError(app, "403", "用户无权限");
        } else {
            response.setStatus(401);
            error = new FeignClientError(app, "401", "用户未登录");
        }

        log.error("AccessDeniedException处理发生错误: {}:{} ", "IllegalArgumentException", e.getMessage(), e);
        error.addInfo("path", request.getServletPath());
        error.addInfo("method", request.getMethod());
        return error;
    }

}
