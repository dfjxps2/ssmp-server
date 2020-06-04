package com.seaboxdata.auth.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seaboxdata.auth.api.controller.IOauthLoginController;
import com.seaboxdata.auth.api.dto.AddressDTO;
import com.seaboxdata.auth.api.dto.LoginUserDTO;
import com.seaboxdata.auth.api.dto.domain.Token;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.dao.OauthUserMapper;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.auth.server.utils.RedisUtil;
import com.seaboxdata.auth.server.utils.RefreshTokenUtils;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author makaiyu
 * @date 2019/5/23 16:05
 */
@Slf4j
@RestController
public class OauthLoginController implements IOauthLoginController {

    @Autowired
    private OauthUserService oauthUserService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RefreshTokenUtils refreshTokenUtils;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Autowired
    private OauthUserMapper oauthUserMapper;

    @Value("${token.domain}")
    private String tokenDomain;

    /**
     * @param loginUserDTO
     * @return com.seaboxdata.commons.utils.Resp
     * @author makaiyu
     * @description 登陆
     * @date 10:35 2019/5/20
     **/
    @Override
    public OauthUserVO loginUser(@RequestBody LoginUserDTO loginUserDTO) {
        return oauthUserService.login(loginUserDTO);
    }

    /**
     * @author makaiyu
     * @description 退出登录
     * @date 13:59 2019/5/21
     **/
    @Override
    public Boolean logout() {
        return oauthUserService.logout();
    }

    /**
     * @param userId
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据用户Id 退出用户
     * @date 09:59 2020-04-07
     **/
    @Override
    public Boolean logoutByUserId(Long userId) {

        Boolean flag = oauthUserService.logoutByUserId(userId);

        if (flag) {
            Cookie accessTokenCookie = new Cookie("access_token", "");
            accessTokenCookie.setPath("/");
            accessTokenCookie.setDomain(tokenDomain);
            accessTokenCookie.setMaxAge(0);
            accessTokenCookie.setHttpOnly(true);
            httpServletResponse.addCookie(accessTokenCookie);

            Cookie refreshTokenCookie = new Cookie("refresh_token", "");
            accessTokenCookie.setPath("/");
            accessTokenCookie.setDomain(tokenDomain);
            accessTokenCookie.setMaxAge(0);
            accessTokenCookie.setHttpOnly(true);
            httpServletResponse.addCookie(refreshTokenCookie);

            return true;
        }

        return false;
    }


    @Override
    public Boolean casLogout() {
        Cookie accessTokenCookie = new Cookie("JSESSIONID", "");
        accessTokenCookie.setPath("/");
//        accessTokenCookie.setDomain("auth-service.dev.jinxin.cloud");
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setHttpOnly(true);
        httpServletResponse.addCookie(accessTokenCookie);
        return true;
    }

    /**
     * @param clientId, clientSecret, refreshToken
     * @return com.seaboxdata.auth.api.dto.domain.Token
     * @author makaiyu
     * @description 根据refreshToken 获取新的accessToken
     * @date 14:47 2019/9/29
     **/
    @Override
    public Token refreshToken(String clientId, String clientSecret, String refreshToken) {
        return refreshTokenUtils.refreshToken(clientId, clientSecret, refreshToken);
    }

    /**
     * @param addressDTO
     * @return void
     * @author makaiyu
     * @description 保存用户登陆日志
     * @date 14:38 2020-01-02
     **/
    @Override
    public void saveAddressLog(AddressDTO addressDTO) {
        oauthUserService.saveAddressLog(addressDTO);
    }

    @Override
    public String testLoginUser(String username, String pwd) {

        LambdaQueryWrapper<OauthUser> eq = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getUsername, username);

        OauthUser user = MapperUtils.getOne(oauthUserMapper, eq, true);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        // 若比对成功
        if (bCryptPasswordEncoder.matches(pwd, user.getPassword())) {
            return "success";
        }

        return "failed";
    }

}
