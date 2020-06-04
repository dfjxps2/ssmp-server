package com.seaboxdata.auth.common;

import lombok.Data;

/**
 * @author hualong
 */
@Data
public class Response {

    private final Boolean success;
    private final String msg;

    public Response(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }
}
