package com.seaboxdata.auth.server.config.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author makaiyu
 * @date 2019/6/25 17:23
 */
@Service
public class MyUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("user_name", authentication);
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            map.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return map;
    }


}

