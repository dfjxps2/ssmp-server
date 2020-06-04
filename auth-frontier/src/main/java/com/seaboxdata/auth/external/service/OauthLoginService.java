package com.seaboxdata.auth.external.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface OauthLoginService {

    String checkoutToken(String redirect_url, HttpServletRequest request, HttpServletResponse resp) throws UnsupportedEncodingException;

    Map<String, Object> freshTokenFromEx(Map<String, String> map);
}
