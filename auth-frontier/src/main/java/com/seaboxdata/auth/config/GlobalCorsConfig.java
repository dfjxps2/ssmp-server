package com.seaboxdata.auth.config;

import com.seaboxdata.auth.config.filter.AuthCorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * @author makaiyu
 * @date 2019/6/19 14:08
 */
@Configuration
@Order(1)
public class GlobalCorsConfig {

    @Bean
    public FilterRegistrationBean authFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setName("authCorsFilter");
        AuthCorsFilter authCorsFilter = new AuthCorsFilter();
        registrationBean.setFilter(authCorsFilter);
        registrationBean.setOrder(1);
        List<String> urlList = new ArrayList<String>();
        urlList.add("/*");
        registrationBean.setUrlPatterns(urlList);
        return registrationBean;
    }
}
