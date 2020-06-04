package com.seaboxdata.auth.server;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author makaiyu
 * @date 2019/5/24 9:51
 */
@SpringBootApplication(scanBasePackages = "com.seaboxdata")
@MapperScan(basePackages = "com.seaboxdata.auth.server.dao")
@EnableTransactionManagement
@EnableFeignClients(basePackages = {"com.seaboxdata.jxpm"})
@EnableDiscoveryClient
public class AuthorizationApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationApplication.class);
    }

    /** * 分页插件 */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * 为了解决CAS登陆成功后跳转回auth后url路径中携带有;jsessionid=系统会报错
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "cas.bj.filter", name = "enabled", havingValue = "true", matchIfMissing = true)
    public HttpFirewall allowUrlSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }
}
