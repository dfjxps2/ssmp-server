package com.seaboxdata.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author makaiyu
 * @date 2019/5/21 14:47
 */
@Slf4j
public class MyAuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private WebResponseExceptionTranslator<?> exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    @Autowired
    private RefreshTokenHandle refreshTokenHandle;

    @Autowired
    private LockService lock;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        try {
            //解析异常，如果是401则处理
            ResponseEntity<?> result = exceptionTranslator.translate(authException);

            refreshTokenHandle.setHeader(request, response);
            String entryPointRefreshTokenLock = "EntryPointRefreshTokenLock";
            String uuid = UUID.randomUUID().toString();
            try {
                if (result.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    if (lock.tryLock(uuid,entryPointRefreshTokenLock)) {
                        refreshTokenHandle.refreshTokenMap(request, response);
                    }
                } else {
                    //如果不是401异常，则以默认的方法继续处理其他异常
                    super.commence(request, response, authException);
                }
            } catch (Exception e) {
                refreshTokenHandle.addTokenCookie(response, "", "access_token", 0);
                refreshTokenHandle.addTokenCookie(response, "", "refresh_token", 0);
            } finally {
                lock.unlock(uuid, entryPointRefreshTokenLock);
            }
        } catch (Exception e) {
            log.warn("error:{}", e.getMessage());
        }
    }

}
