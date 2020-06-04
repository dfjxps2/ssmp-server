package com.seaboxdata.auth.server.config.resources;

import com.seaboxdata.auth.server.config.filter.MyBearerTokenExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;

/**
 * @author makaiyu
 * @date 2019/5/15 18:05
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/authentication/form").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/oauth/**").permitAll()
                .antMatchers("/**").permitAll()
                .antMatchers("/graphql").permitAll()
                .antMatchers("/graphiql").permitAll()
                .anyRequest().authenticated()
                .and().logout().permitAll();
    }

    @Bean
    public TokenExtractor myBearerTokenExtractor() {
        return new MyBearerTokenExtractor();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resource) throws Exception {
        resource.tokenExtractor(myBearerTokenExtractor());
    }
}
