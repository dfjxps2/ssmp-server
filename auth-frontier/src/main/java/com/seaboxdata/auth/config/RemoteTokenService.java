package com.seaboxdata.auth.config;

import com.seaboxdata.auth.api.enums.UrlEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author makaiyu
 * @date 2019/7/24 11:16
 */
@Slf4j
public class RemoteTokenService extends RemoteTokenServices {

    protected final Log logger = LogFactory.getLog(getClass());

    private String tokenName = "token";

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${security.oauth2.resource.check-token}")
    private String checkTokenEndpointUrl;

    @Value("${seaboxdata.auth.url}")
    private String authUrl;

    @Value("${token.access-token.expiration-time}")
    private Integer accessTokenExpirationTime;

    @Value("${token.refresh-token.expiration-time}")
    private Integer refreshTokenExpirationTime;

    @Value("${cookie.access-cookie-time}")
    private Integer accessCookieTime;

    @Value("${token.domain}")
    private String tokenDomain;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LockService lock;

    @Autowired
    private RefreshTokenHandle refreshTokenHandle;

    /**
     * 鉴权模式
     */
    public static final String[] GRANT_TYPE = {"password", "refresh_token", "access_token"};

    private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
        formData.add(tokenName, accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAuthorizationHeader(clientId, clientSecret));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        Map<String, Object> map = new HashMap<>();

        try {
            map = restTemplate.exchange(authUrl + UrlEnum.CHECK_TOKEN_URL.getUrl(), HttpMethod.POST,
                    new HttpEntity<>(formData, headers), Map.class).getBody();
        } catch (Exception e) {
            String refreshTokenLock = "refreshTokenLock";
            String uuid = UUID.randomUUID().toString();
            try {
                if (lock.tryLock(uuid,refreshTokenLock)) {
                    log.info("获取到锁啦！！！");
                    HttpServletRequest request;
                    HttpServletResponse response;
                    try {
                        request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                        response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                    } catch (Exception e1) {
                        return tokenConverter.extractAuthentication(map);
                    }

                    map = refreshTokenHandle.refreshTokenMap(request, response);
                    String newAccessToken = "";
                    String newRedisAccessToken = "";
                    if (map.containsKey(GRANT_TYPE[2])) {
                        newAccessToken = (String) map.get("access_token");
                        newRedisAccessToken = "Bearer" + map.get("access_token");
                        formData.set(tokenName, newAccessToken);
                        refreshTokenHandle.addTokenCookie(response, newRedisAccessToken, "access_token", accessCookieTime);
                    }

                    String newRefreshToken = "";
                    if (map.containsKey(GRANT_TYPE[1])) {
                        newRefreshToken = (String) map.get("refresh_token");
                        refreshTokenHandle.addTokenCookie(response, newRefreshToken, "refresh_token", accessCookieTime);
                    }
                    if (StringUtils.isEmpty(newAccessToken)) {
                        logger.warn("new Token is null!!!");
                    }

                    map = restTemplate.exchange(authUrl + UrlEnum.CHECK_TOKEN_URL.getUrl(), HttpMethod.POST,
                            new HttpEntity<>(formData, headers), Map.class).getBody();
                }

                log.info("auth-frontier 未获取到锁");

                // gh-838
                if (!Boolean.TRUE.equals(map.get("active"))) {
                    logger.debug("check_token returned active attribute: " + map.get("active"));
                    throw new InvalidTokenException(accessToken);
                }
            } catch (Exception e1) {
                log.warn("刷新token后 check_token出异常啦！！！ : {} ", e1.getMessage());
                HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                assert resp != null;
                refreshTokenHandle.addTokenCookie(resp, "", "access_token", 0);
                refreshTokenHandle.addTokenCookie(resp, "", "refresh_token", 0);
            } finally {
                lock.unlock(uuid, refreshTokenLock);
            }
        }
        return tokenConverter.extractAuthentication(map);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

    private String getAuthorizationHeader(String clientId, String clientSecret) {

        if (clientId == null || clientSecret == null) {
            logger.warn("Null Client ID or Client Secret detected. Endpoint that requires authentication will reject request with 401 error.");
        }

        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

}
