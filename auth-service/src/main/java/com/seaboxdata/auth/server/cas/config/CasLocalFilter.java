package com.seaboxdata.auth.server.cas.config;

import com.seaboxdata.auth.server.cas.utils.CasUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zdl
 * @date 2019/9/17 15:40
 */
@Slf4j
public class CasLocalFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String username = CasUserUtils.getCasLoginUsername(request);
        log.info("CAS doFilterInternal -> username : {}", username);

        filterChain.doFilter(request, response);
    }
}
