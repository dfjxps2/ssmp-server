package com.seaboxdata.auth.server.utils;

public class AssertUtil {
    /**
     * @param authToken
     * @return
     * @description 获取请求头的token
     */
    public static String extracteToken(String authToken) {
        String authTokenPrefix = "Bearer";
        if (authToken.contains(authTokenPrefix)) {
            return authToken.substring(7);
        } else {
            return authToken;
        }
    }
}
