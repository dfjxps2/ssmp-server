package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @description 登录用户传输参数
 * @date 2019/5/20 10:37
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class LoginUserDTO implements Serializable {

    private static final long serialVersionUID = -4658151327349732347L;
    /** 登陆账号 */
    private String username;

    /** 用户密码 */
    private String password;

    /** 一周免登陆 */
    private Boolean rememberMe;

    /** 客户端Id */
    private String clientId;

    /** 客户端密钥 */
    private String clientSecret;

    /** 用户名 */
    private String name;

    /** accessToken码 */
    private String accessToken;

    /** refreshToken码 */
    private String refreshToken;

}
