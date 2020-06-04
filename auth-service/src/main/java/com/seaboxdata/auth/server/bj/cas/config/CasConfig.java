package com.seaboxdata.auth.server.bj.cas.config;

import com.seaboxdata.auth.server.bj.cas.synchrodata.CasUserUtils;
import com.seaboxdata.cas.client.CustCas30ServiceValidatorFilter;
import org.apache.axis2.addressing.EndpointReference;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cc on 2018/11/27.
 */
@Configuration
@Component
@ConditionalOnProperty(prefix = "cas.bj.filter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CasConfig {

    // http://10.1.3.2:17001/cas
    @Value("${cas.bj.server.prefix}")
    private String casServerUrl;

    // http://mkyauth.jinxin.cloud:8080
    @Value("${cas.bj.server.project}")
    private String projectUrl;

    // http://10.1.3.2:18001/portal/intlDataSynchronizedservice?wsdl
    @Value("${cas.bj.ws.url}")
    private String soapWsdlAddress;

    // /bj/cas/authDoLogin
    @Value("${cas.bj.intercept}")
    private String intercept;

    /**
     * 该过滤器用于实现单点登出功能，单点退出配置，一定要放在其他filter之前
     */
    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new SingleSignOutFilter());
//        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setUrlPatterns(CasUserUtils.urlPatterns(intercept));
        filterRegistration.addInitParameter("casServerUrlPrefix", casServerUrl + "/logout?locale=zh_CN");
        filterRegistration.addInitParameter("service", casServerUrl);
        filterRegistration.setOrder(Integer.MAX_VALUE - 9);
        return filterRegistration;
    }

    /**
     * 用于实现单点登出功能
     */
    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listener = new ServletListenerRegistrationBean<>();
        listener.setListener(new SingleSignOutHttpSessionListener());
        listener.setOrder(Integer.MAX_VALUE - 10);
        return listener;
    }

    /**
     * 该过滤器负责用户的认证工作
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean authenticationFilterRegistrationBean() {
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new AuthenticationFilter());
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerLoginUrl", casServerUrl + "/login?locale=zh_CN");
        initParameters.put("service", projectUrl);
        authenticationFilter.setInitParameters(initParameters);
        authenticationFilter.setOrder(Integer.MAX_VALUE - 8);
//        List<String> urlPatterns = new ArrayList<>();
//        urlPatterns.add("/");
//        authenticationFilter.setUrlPatterns(urlPatterns);
        authenticationFilter.setUrlPatterns(CasUserUtils.urlPatterns(intercept));
        return authenticationFilter;
    }

    /**
     * 该过滤器负责对Ticket的校验工作
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean ValidationFilterRegistrationBean() {
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new CustCas30ServiceValidatorFilter());
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerUrlPrefix", casServerUrl);
        initParameters.put("service", projectUrl);
        authenticationFilter.setInitParameters(initParameters);
        authenticationFilter.setOrder(Integer.MAX_VALUE - 7);
        authenticationFilter.setUrlPatterns(CasUserUtils.urlPatterns(intercept));
        return authenticationFilter;
    }

    /**
     * 该过滤器对HttpServletRequest请求包装， 可通过HttpServletRequest的getRemoteUser()方法获得登录用户的登录名
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean casHttpServletRequestWrapperFilter() {
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new HttpServletRequestWrapperFilter());
        authenticationFilter.setOrder(Integer.MAX_VALUE - 6);
        authenticationFilter.setUrlPatterns(CasUserUtils.urlPatterns(intercept));
        return authenticationFilter;
    }

    /**
     * 该过滤器使得可以通过org.jasig.cas.client.util.AssertionHolder来获取用户的登录名。
     * 比如AssertionHolder.getAssertion().getPrincipal().getName()。
     * 这个类把Assertion信息放在ThreadLocal变量中，这样应用程序不在web层也能够获取到当前登录信息
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean casAssertionThreadLocalFilter() {
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new AssertionThreadLocalFilter());
        authenticationFilter.setOrder(Integer.MAX_VALUE - 5);
        authenticationFilter.setUrlPatterns(CasUserUtils.urlPatterns(intercept));
        return authenticationFilter;
    }

    /**
     * 本地自定义过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean casLocalFilter() {
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new CasLocalFilter());
        authenticationFilter.setOrder(Integer.MAX_VALUE - 4);
        authenticationFilter.setUrlPatterns(CasUserUtils.urlPatterns(intercept));
        return authenticationFilter;
    }

    @Bean
    public EndpointReference endpointReference() {
        return new EndpointReference(soapWsdlAddress);
    }
}