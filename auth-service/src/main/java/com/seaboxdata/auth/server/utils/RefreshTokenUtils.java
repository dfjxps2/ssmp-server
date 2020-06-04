package com.seaboxdata.auth.server.utils;

import com.seaboxdata.auth.api.dto.domain.Token;
import com.seaboxdata.auth.api.enums.UrlEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author makaiyu
 * @date 2019/6/6 15:56
 */
@Component
@Slf4j
public class RefreshTokenUtils {

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${seaboxdata.auth.url}")
    private String authUrl;

    /**
     * 鉴权模式
     */
    public static final String[] GRANT_TYPE = {"password", "refresh_token"};

    public Token refreshToken(String clientId, String clientSecret, String refreshToken) {

        Token token = new Token();
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", clientId);
        paramMap.add("client_secret", clientSecret);
        paramMap.add("refresh_token", refreshToken);
        paramMap.add("grant_type", GRANT_TYPE[1]);
        Map<String, Object> countryMap = handleTokenModel(paramMap);
        try {
            DataHelper.putDataIntoEntity(countryMap, token);
        } catch (Exception e) {
            log.warn("error:{}", e.getMessage());
        }
        return token;
    }

    public Map<String, Object> handleTokenModel(MultiValueMap<String, Object> paramMap) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap, requestHeaders);
        ResponseEntity<String> responseEntity;
        try {
            log.info("httpEntity : {} " , httpEntity);
            log.info("url : {} " , authUrl + UrlEnum.LOGIN_URL.getUrl());
            responseEntity = restTemplate.exchange(authUrl + UrlEnum.LOGIN_URL.getUrl(), HttpMethod.POST, httpEntity, String.class);
        } catch (Exception e) {
            log.warn("handleTokenModel -> error: {} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        String body = responseEntity.getBody();
        String cms = body.replace("{", "").replace("}", "");
        String[] countryMapStr = cms.split(",");
        Map<String, Object> countryMap = new HashMap<>();
        for (String s : countryMapStr) {
            System.err.println("s:" + s);
            String quotation = s.replaceAll("\"", "");
            String[] ms = quotation.split(":");
            countryMap.put(ms[0], ms[1]);
        }
        return countryMap;
    }
}

