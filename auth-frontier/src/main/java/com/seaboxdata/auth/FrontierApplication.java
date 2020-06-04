package com.seaboxdata.auth;

import com.coxautodev.graphql.tools.SchemaParserOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @author makaiyu
 * @date 2019/5/22 15:17
 */
@SpringBootApplication
@EnableFeignClients("com.seaboxdata")
@EnableDiscoveryClient
public class FrontierApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontierApplication.class);
    }

    @Bean
    public SchemaParserOptions schemaParserOptions() {
        //临时设置，允许graphql不强制所有resolver均提供实现
        return SchemaParserOptions.newOptions()
                .allowUnimplementedResolvers(true).build();
    }

}
