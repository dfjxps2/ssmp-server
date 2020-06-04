package com.seaboxdata.auth.config;

import com.seaboxdata.auth.api.enums.UrlEnum;
import com.seaboxdata.auth.utlis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author makaiyu
 * @date 2019/8/13 13:29
 */
@Component
@Slf4j
public class RefreshTokenHandle {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${seaboxdata.auth.url}")
    private String authUrl;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${token.access-token.expiration-time}")
    private Integer accessTokenExpirationTime;

    @Value("${token.refresh-token.expiration-time}")
    private Integer refreshTokenExpirationTime;

    @Value("${token.domain}")
    private String tokenDomain;

    @Value("${cookie.access-cookie-time}")
    private Integer accessCookieTime;

    /**
     * 鉴权模式
     */
    public static final String[] GRANT_TYPE = {"password", "refresh_token", "access_token"};

    public Map<String, Object> refreshTokenMap(HttpServletRequest request, HttpServletResponse response) {

        assert response != null;
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", GRANT_TYPE[1]);
        Cookie[] cookie = request.getCookies();
        // 获取本次请求中携带的refresh_token
        String refreshToken = "";
        String accessToken = "";
        if (Objects.nonNull(cookie)) {
            for (Cookie coo : cookie) {
                if (GRANT_TYPE[1].equals(coo.getName())) {
                    refreshToken = coo.getValue();
                    formData.add("refresh_token", refreshToken);
                }
                if (GRANT_TYPE[2].equals(coo.getName())) {
                    accessToken = coo.getValue();
                }
            }
        }

        log.info("accessToken : {}", accessToken);
        log.info("refreshToken : {}", refreshToken);

        Map<String, Object> map = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(accessToken)) {
            map = (Map<String, Object>) redisUtil.getValueByKey("Redis" + accessToken);
            if (!CollectionUtils.isEmpty(map)) {
                return map;
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 尝试从Redis中获取以本次请求的refreshToken为key的新的refreshToken
        String refreshTokenByKey = (String) redisUtil.getValueByKey(refreshToken);
        String accessTokenByKey = (String) redisUtil.getValueByKey(accessToken);

        // 如果数据库中不存在  则发送请求刷新Token
        if (StringUtils.isEmpty(refreshTokenByKey)) {
            if (Objects.nonNull(formData.get(GRANT_TYPE[1]))) {
                try {
                    map = restTemplate.exchange(authUrl + UrlEnum.LOGIN_URL.getUrl(), HttpMethod.POST,
                            new HttpEntity<>(formData, headers), Map.class).getBody();
                    map.put("active", true);
                } catch (Exception e) {
                    response.setStatus(401);
                }

                // 将map放入Redis  取出时 若有值  直接返回
                if (!StringUtils.isEmpty(accessToken)) {
                    if (!CollectionUtils.isEmpty(map)) {
                        redisUtil.set("Redis" + accessToken, map);
                        log.info("放入redis时 key : {}", accessToken);
                    }
                }

                // 获取刷新的RefreshToken
                String newRefreshToken = (String) map.get("refresh_token");
                log.info("刷新后 newRefreshToken:{}", newRefreshToken);
                String newAccessToken = "Bearer" + map.get("access_token");
                log.info("刷新后 newAccessToken:{}", newRefreshToken);

                //如果刷新异常,则坐进一步处理
                if (map.get("error") != null) {
                    // 返回指定格式的错误信息
                    response.setStatus(401);
                    response.setHeader("Content-Type", "application/json;charset=utf-8");
                } else {
                    if (map.containsKey(GRANT_TYPE[2])) {
                        addTokenCookie(response, newAccessToken, "access_token", accessCookieTime);
                        redisUtil.set(accessToken, newAccessToken, refreshTokenExpirationTime);
                    }
                    if (map.containsKey(GRANT_TYPE[1])) {
                        addTokenCookie(response, newRefreshToken, "refresh_token", refreshTokenExpirationTime);
                        redisUtil.set(refreshToken, newRefreshToken, refreshTokenExpirationTime);
                        response.setStatus(1401);
                    }
                }
            } else {
                addTokenCookie(response, accessTokenByKey, "access_token", accessCookieTime);
                addTokenCookie(response, refreshTokenByKey, "refresh_token", refreshTokenExpirationTime);
                response.setStatus(1401);
            }
        }
        return map;
    }


    public void setHeader(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Max-Age", "3601");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
    }

    public void addTokenCookie(HttpServletResponse response, String accessToken, String tokenName, int i) {
        Cookie accessTokenCookie = new Cookie(tokenName, accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setDomain(tokenDomain);
        accessTokenCookie.setMaxAge(i);
        accessTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);
    }

}
