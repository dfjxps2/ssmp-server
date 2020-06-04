package com.seaboxdata.auth.server.bj.cas.controller;

import com.seaboxdata.auth.api.dto.LoginUserDTO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.bj.cas.service.ISynchDataService;
import com.seaboxdata.auth.server.bj.cas.synchrodata.CasUserUtils;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.commons.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class BjCasController {

    @Autowired
    private ISynchDataService synchDataService;

    @Autowired
    private OauthUserService oauthUserService;

    @Value("${token.domain}")
    private String tokenDomain;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${cas.bj.home}")
    private String home;

    @GetMapping("/bj/cas/authDoLogin")
    public void handleFoo1(@RequestParam(value = "redirect", required = false) String redirect,
                           HttpServletResponse response, HttpServletRequest request) throws IOException {

        // 获取CAS登录用户username
        String username = CasUserUtils.getCasLoginUsername(request);

        // 查询本地auth没有username的数据，可以实时查询user的数据后做同步
        Boolean flag = synchDataService.checkLoginByCasUsername(username);

        // 然后进行伪登录
        if (!flag) {
            throw new ServiceException("500", username + "登录失败");
        }

        LoginUserDTO loginUserDTO = new LoginUserDTO()
                .setUsername(username)
                .setPassword(CasUserUtils.PASSWORD)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRememberMe(true);
        OauthUserVO oauthUserVO = oauthUserService.login(loginUserDTO);

        Cookie accessTokenCookie = new Cookie("access_token", "Bearer" + oauthUserVO.getAccessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setDomain(tokenDomain);
        accessTokenCookie.setMaxAge(60 * 60 * 12 * 2 * 7);
        accessTokenCookie.setHttpOnly(true);

        Cookie refreshTokenCookie = new Cookie("refresh_token", oauthUserVO.getRefreshToken());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setDomain(tokenDomain);
        refreshTokenCookie.setMaxAge(60 * 60 * 12 * 2 * 30);
        refreshTokenCookie.setHttpOnly(true);

        Cookie jsessionId = new Cookie("JSESSIONID", "");
        jsessionId.setPath("/");
        jsessionId.setMaxAge(0);
        jsessionId.setHttpOnly(true);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        response.addCookie(jsessionId);
        oauthUserVO.setAccessToken("");
        oauthUserVO.setRefreshToken("");

        if (StringUtils.isEmpty(redirect)) {
            redirect = home;
        }

        response.sendRedirect(redirect);
    }
}
