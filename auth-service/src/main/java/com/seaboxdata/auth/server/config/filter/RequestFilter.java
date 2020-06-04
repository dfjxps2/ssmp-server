package com.seaboxdata.auth.server.config.filter;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.seaboxdata.auth.api.utils.domain.UserDetail;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.config.service.UserDetailsServiceImpl;
import com.seaboxdata.auth.server.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author makaiyu
 * @date 2019/6/27 15:40
 */
@Slf4j
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 其他服务取Feign添加到Header里的值
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String token = request.getHeader(name);
                    if ("access_token".equalsIgnoreCase(name)) {
                        if (StringUtils.isEmpty(token)) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
                            return;
                        }
                        OauthUserVO loginUserVO = (OauthUserVO) redisUtil.getValueByKey(token);

                        if (Objects.nonNull(loginUserVO)) {
                            String username = loginUserVO.getUsername();
                            UserDetail userDetails = (UserDetail) userDetailsService.loadUserByUsername(username);

                            // 创建Authentication
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            // SecurityContext中存入Authentication
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }
}
