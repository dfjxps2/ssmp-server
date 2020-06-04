package com.seaboxdata.auth.api.dto.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author makaiyu
 * @description oauth2客户端token参数
 * @date 2019/5/20 10:42
 */
@Data
public class Token implements Serializable {

    private static final long serialVersionUID = -1069527060142021725L;

    /** 过期时限 */
    private String expiresIn;

    /** refreshToken */
    private String refreshToken;

    /** token类型 */
    private String tokenType;

    /** access_token值 */
    private String accessToken;

    /** 使用范围 */
    private String scope;
}
