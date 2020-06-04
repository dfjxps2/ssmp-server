package com.seaboxdata.auth.external.service.impl;

import com.seaboxdata.auth.api.enums.UrlEnum;
import com.seaboxdata.auth.config.RefreshTokenHandle;
import com.seaboxdata.auth.external.service.OauthLoginService;
import com.seaboxdata.auth.utlis.RedisUtil;
import com.seaboxdata.auth.utlis.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class OauthLoginServiceImpl implements OauthLoginService {

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${seaboxdata.auth.url}")
    private String authUrl;


    @Value("${login.method}")
    private String loginMethod;

    @Value("${login.url.auth}")
    private String loginUrlAuth;

    @Value("${login.url.cas}")
    private String loginUrlCas;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RefreshTokenHandle refreshTokenHandle;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 鉴权模式
     */
    public static final String[] GRANT_TYPE = {"password", "refresh_token", "access_token"};

    @Override
    public String checkoutToken(String redirect_url, HttpServletRequest request, HttpServletResponse resp) throws UnsupportedEncodingException {
        Cookie[] cookieAuth = request.getCookies();
        String accessToken = "";
        String newRefreshToken = "";
        if (Objects.nonNull(cookieAuth)) {
            for (Cookie coo : cookieAuth) {
                if (GRANT_TYPE[1].equals(coo.getName())) {
                    newRefreshToken = coo.getValue();
                }
                if (GRANT_TYPE[2].equals(coo.getName())) {
                    accessToken = coo.getValue();
                }
            }
        }
        if(StringUtils.isNotBlank(accessToken)){
            accessToken = accessToken.replace("Bearer", "").replace("bearer", "");
            //token存在时获取验证token的
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
            formData.add("token", accessToken);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getAuthorizationHeader2(clientId, clientSecret));
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            Map<String, Object> map = new HashMap<>();

            try {
                map = restTemplate.exchange(authUrl + UrlEnum.CHECK_TOKEN_URL.getUrl(), HttpMethod.POST, new HttpEntity<>(formData, headers), Map.class).getBody();
                //error -> invalid_token
                if("invalid_token".equals(map.get("error"))){
                    log.error("令牌一次失效");
                    accessToken = null;
                    map = refreshTokenHandle.refreshTokenMap(request, resp);
                }
            } catch (Exception e) {
                log.error("令牌一次校验异常", e);
                try {
                    map = refreshTokenHandle.refreshTokenMap(request, resp);
                }catch (Exception e2){
                    log.error("令牌二次失效");
                }
                accessToken = null;
            }

            // 获取刷新的RefreshToken
            if (map.containsKey(GRANT_TYPE[2])) {
                accessToken = (String) map.get("access_token");
            }
            if (map.containsKey(GRANT_TYPE[1])) {
                newRefreshToken = (String) map.get("refresh_token");
            }

        }

        if(StringUtils.isBlank(accessToken)){
            redirect_url = URLEncoder.encode(redirect_url, "UTF-8");
            String gotoUrl = "";

            //token为空或者失效，跳转至登录页面
            // 采用登录方式，AUTH（数研登录方式），CAS（采用数研的登录方式）
            if("AUTH".equals(loginMethod)){
                gotoUrl = loginUrlAuth + "?redirect_url=" + redirect_url;
            }else if("CAS".equals(loginMethod)){
                gotoUrl = loginUrlCas + "?redirect=" + redirect_url;
            }
            return gotoUrl;
        }
        //令牌有效，跳转回原路径
        return UrlUtils.urlAddToken(redirect_url, "Bearer" + accessToken, newRefreshToken);
    }

    private String getAuthorizationHeader2(String clientId, String clientSecret) {

        if (clientId == null || clientSecret == null) {

        }

        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

    @Override
    public Map<String, Object> freshTokenFromEx(Map<String, String> reqMap){
        Map<String, Object> resMap = new HashMap<>();
        if(clientId.equals(reqMap.get("clientId")) && clientSecret.equals(reqMap.get("clientSecret"))){

            String accessToken = reqMap.get("accessToken");
            String refreshToken = reqMap.get("refreshToken");

            if (!org.springframework.util.StringUtils.isEmpty(accessToken)) {
                resMap = (Map<String, Object>) redisUtil.getValueByKey("Redis" + accessToken);
                if (!CollectionUtils.isEmpty(resMap)) {
                    return resMap;
                }
            }

            // 尝试从Redis中获取以本次请求的refreshToken为key的新的refreshToken
            String refreshTokenByKey = (String) redisUtil.getValueByKey(refreshToken);
            String accessTokenByKey = (String) redisUtil.getValueByKey(accessToken);

            // 如果数据库中不存在  则发送请求刷新Token
            if (org.springframework.util.StringUtils.isEmpty(refreshTokenByKey)) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
                formData.add("client_id", clientId);
                formData.add("client_secret", clientSecret);
                formData.add("grant_type", GRANT_TYPE[1]);
                formData.add("refresh_token", refreshToken);

                try {
                    resMap = restTemplate.exchange(authUrl + UrlEnum.LOGIN_URL.getUrl(), HttpMethod.POST,
                            new HttpEntity<>(formData, headers), Map.class).getBody();
                    resMap.put("active", true);
                } catch (Exception e) {

                }

                // 将map放入Redis  取出时 若有值  直接返回
                if (!org.springframework.util.StringUtils.isEmpty(accessToken)) {
                    if (!CollectionUtils.isEmpty(resMap)) {
                        redisUtil.set("Redis" + accessToken, resMap);
                    }
                }
            }

        }else{
            resMap.put("error", "clientId和clientSecret信息有误");
        }
        return resMap;
    }
}
