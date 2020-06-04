package com.seaboxdata.auth.server.bj.cas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author makaiyu
 * @date 2019/10/18 9:46
 */
@Component
@ConfigurationProperties(prefix = "cas.bj.ws")
public class SoapWsdlAddressConfig {

    /** 同步url */
    private String url;

    /** 同步系统名称 */
    private String appname;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
