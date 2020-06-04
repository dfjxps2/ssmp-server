package com.seaboxdata.auth.api.enums;

/**
 * @author makaiyu
 * @description: oauth url 枚举类
 * @date 2019/5/20 10:42
 */
public enum UrlEnum {

    //oauth2登录
    LOGIN_URL("/oauth/token"),
    CHECK_TOKEN_URL("/oauth/check_token"),

    ;

    private String url;

    UrlEnum(String url) {
        this.url = url;

    }


    public String getUrl() {
        return url;
    }
}
