package com.seaboxdata.auth.exception;

import com.seaboxdata.commons.exception.ServiceException;
import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceAccessError implements GraphQLError {
    private String app;
    private String code;
    private String msg;
    private Map<String, String> infos;

    public ServiceAccessError(ServiceException e) {
        this.app = e.getApp();
        this.code = e.getCode();
        this.msg = e.getMessage();
        if (!CollectionUtils.isEmpty(e.getInfos())) {
            infos = new HashMap<>(e.getInfos());
        }
    }

    public ServiceAccessError(String app, String code, String msg) {
        this.app = app;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ServiceExceptionErrorType.SERVICE_ERROR;
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("app", app);
        extensions.put("code", code);

        if (infos != null) {
            extensions.putAll(infos);
        }
        return extensions;
    }

    enum ServiceExceptionErrorType implements ErrorClassification {
        SERVICE_ERROR
    }

}
