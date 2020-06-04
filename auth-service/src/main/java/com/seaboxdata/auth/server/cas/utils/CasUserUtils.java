package com.seaboxdata.auth.server.cas.utils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

public class CasUserUtils {

    /**
     * 实时同步数据固定的username管理员
     */
    public static final String ADMIN_USER = "CasAdmin";

    /**
     * 数据同步固定初始化密码
     */
    public static final String PASSWORD = "123123";

    /**
     * 此方法必须在controller中使用
     */
    public static String getCasLoginUsername(HttpServletRequest request) {
        String username = request.getRemoteUser();
        if(StringUtils.isNotBlank(username))
            return username;
        Principal pal = request.getUserPrincipal();
        if(pal != null){
            username = pal.getName();
            if(username != null)
                return username;
        }
        Object obj = request.getAttribute("credentials");
        if(obj != null){
            return obj.toString();
        }
        return username;
    }

    /**
     * 过滤器拦截路径
     */
    private static List<String> interceptsUrls;
    public synchronized static List<String> urlPatterns(String intercepts){
        if(null == interceptsUrls){
            String[] urls = intercepts.split(",");
            interceptsUrls = Arrays.asList(urls);
        }
        return interceptsUrls;
    }
}
