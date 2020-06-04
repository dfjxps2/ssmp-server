package com.seaboxdata.auth.config;

import com.seaboxdata.auth.config.filter.AuthSecurityContextFilter;
import com.seaboxdata.auth.config.filter.MyBearerTokenExtractor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * @author makaiyu
 * @date 2019/5/15 18:05
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/authentication/form").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/oauth/**").permitAll()
                .antMatchers("/**").permitAll()
                .antMatchers("/graphql").permitAll()
                .anyRequest().authenticated()
                .and().logout().permitAll();
        http.addFilterAfter(authSecurityContextFilter(), FilterSecurityInterceptor.class);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        return restTemplate;
    }

    @Bean
    public AuthSecurityContextFilter authSecurityContextFilter() {
        return new AuthSecurityContextFilter();
    }

    @Bean
    @Primary
    public RemoteTokenServices myRemoteTokenServices() {
        return new RemoteTokenService();
    }

    @Bean
    public TokenExtractor myBearerTokenExtractor() {
        return new MyBearerTokenExtractor();
    }

    @Bean
    public AuthenticationEntryPoint myAuthenticationEntryPoint() {
        return new MyAuthenticationEntryPoint();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resource) throws Exception {
        resource.tokenExtractor(myBearerTokenExtractor());
        resource.tokenServices(myRemoteTokenServices());
        resource.authenticationEntryPoint(myAuthenticationEntryPoint());
    }
}
