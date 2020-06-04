package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/6/5 17:33
 */
@Data
public class LoginUserInput {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 一周免登陆 */
    private Boolean rememberMe;

}
