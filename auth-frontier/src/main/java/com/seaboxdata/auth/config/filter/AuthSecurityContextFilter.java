package com.seaboxdata.auth.config.filter;

import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.controller.IOauthRoleController;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author makaiyu
 * @date 2019/8/8 16:01
 */
@Slf4j
public class AuthSecurityContextFilter implements Filter {

    @Autowired
    private IOauthRoleController oauthRoleController;

    private static final String FILTER_APPLIED = "__spring_security_demoFilter_filterApplied";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (request.getAttribute(FILTER_APPLIED) != null) {
            chain.doFilter(request, response);
            return;
        }

        OauthLoginUserVO userDetails = new OauthLoginUserVO();
        try {
            userDetails = UserUtils.getUserDetails();
        } catch (Exception e) {
            userDetails = null;
        }

        if (Objects.nonNull(userDetails)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            List<GrantedAuthority> grantedAuthorities = Lists.newArrayListWithCapacity(auth.getAuthorities().size());
            Set<String> permissionCodes = oauthRoleController.selectPermissionCodeByUserId(userDetails.getUserId());
            // 声明用户授权
            permissionCodes.forEach(permissionCode -> {
                if (!StringUtils.isEmpty(permissionCode)) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permissionCode);
                    grantedAuthorities.add(grantedAuthority);
                }
            });
            Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), grantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        request.setAttribute(FILTER_APPLIED, true);

        chain.doFilter(request, response);
    }
}
