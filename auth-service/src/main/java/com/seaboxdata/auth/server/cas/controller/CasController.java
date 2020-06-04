package com.seaboxdata.auth.server.cas.controller;

import com.seaboxdata.auth.api.dto.LoginUserDTO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.cas.model.CasOrganize;
import com.seaboxdata.auth.server.cas.service.ICasService;
import com.seaboxdata.auth.server.cas.service.ISynchDataService;
import com.seaboxdata.auth.server.cas.utils.CasUserUtils;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.commons.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class CasController {

    @GetMapping("/auth/test")
    public List<CasOrganize> test(){
        return casService.synchroOrgData();
    }

    /**
     * CAS登录后跳转回auth的路径
     * btoa()为将url转为base64
     * 未登录时前端可以配置跳转如下
     * let reto = "http://localhost:30002/authDoLogin/" + btoa(location.href)
     * let url = `http://118.126.103.216:17001/cas/login?locale=zh_CN&service=${encodeURIComponent(reto)}`
     * location.href = url
     */
//    @GetMapping("/authDoLogin/{redirect}")
//    public void authDoLogin(@PathVariable("redirect") String redirect,
//                          HttpServletResponse response, HttpServletRequest request) throws IOException {
//        /**
//         * 获取CAS登录用户username
//         */
//        String username = CasUserUtils.getCasLoginUsername(request);
//
//        //查询本地auth没有username的数据，可以实时查询user的数据后做同步
//        CasUser casUser = casService.casUser(username);
//
//        /**
//         * 然后进行伪登录
//         */
//
//
//        /**
//         * 解码需要跳转的url后进行跳转
//         * 如果用redirect作为参数返回的url中总是携带ticket，会报证书校验出错，浦城的运维定位后也找不出问题，
//         * 暂时这么处理，后期再看是证书问题还是别的问题
//         */
//        redirect = Base64Utils.base64ToString(redirect);
//        response.sendRedirect(redirect);
//    }

//    @GetMapping("/authDoLogin1")
//    public void handleFoo11(@RequestParam(value = "redirect1", required = false) String redirect,
//                           @RequestParam(value = "ticket", required = false) String ticket,
//                          HttpServletResponse response, HttpServletRequest request) throws IOException {
//        String username = CasUserUtils.getCasLoginUsername(request);
//        response.sendRedirect(redirect);
//    }

    @GetMapping("/cas/authDoLogin")
    public void handleFoo1(@RequestParam(value = "redirect", required = false) String redirect,
                           HttpServletResponse response, HttpServletRequest request) throws IOException {

        /**
         * 获取CAS登录用户username
         */
        String username = CasUserUtils.getCasLoginUsername(request);

        //查询本地auth没有username的数据，可以实时查询user的数据后做同步
        Boolean flag = synchDataService.checkLoginByCasUsername(username);

        /**
         * 然后进行伪登录
         */
        if(!flag){
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

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        oauthUserVO.setAccessToken("");
        oauthUserVO.setRefreshToken("");

        response.sendRedirect(redirect);
    }

    @GetMapping("/auth/synch/{password}")
    public String synchData(@PathVariable("password") String password){
        if("dfjinxindrd".equals(password)){
            synchDataService.synchData();
            return "同步完成";
        }
        return "密码错误，同步失败";
    }

    @Autowired
    private ICasService casService;

    @Autowired
    private ISynchDataService synchDataService;

    @Value("${token.domain}")
    private String tokenDomain;

    @Autowired
    private OauthUserService oauthUserService;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;
}
