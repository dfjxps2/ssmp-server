package com.seaboxdata.auth.external.controller;

import com.seaboxdata.auth.external.service.OauthLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
public class FrontierOauthLoginController {

    @Autowired
    private OauthLoginService oauthLoginService;

    @GetMapping("/frontier/auth/toLogin")
    public void toLogin(@RequestParam("redirect_url") String redirect_url) throws IOException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        String redirect = oauthLoginService.checkoutToken(redirect_url, servletRequestAttributes.getRequest(), response);
        response.sendRedirect(redirect);
    }

    @PostMapping("/frontier/auth/freshToken")
    public Map<String, Object> frontierAuthFreshToken(@RequestBody Map<String, String> map) {
        return oauthLoginService.freshTokenFromEx(map);
    }

}
