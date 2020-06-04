package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.AddressDTO;
import com.seaboxdata.auth.api.dto.LoginUserDTO;
import com.seaboxdata.auth.api.dto.domain.Token;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author makaiyu
 * @date 2019/5/23 15:58
 */
@FeignClient(contextId = "AuthLogin",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthLoginController {

    /**
     * @param loginUserDTO
     * @return com.seaboxdata.commons.utils.Resp
     * @author makaiyu
     * @description 登陆
     * @date 10:35 2019/5/20
     **/
    @PostMapping(value = "/login/user")
    OauthUserVO loginUser(@RequestBody LoginUserDTO loginUserDTO);

    /**
     * @return com.seaboxdata.commons.utils.Resp
     * @author makaiyu
     * @description 退出登录
     * @date 13:59 2019/5/21
     **/
    @GetMapping("/logout/user")
    Boolean logout();

    /**
     * @param userId
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据用户Id 退出用户
     * @date 09:59 2020-04-07
     **/
    @GetMapping("/logout/user/id")
    Boolean logoutByUserId(@RequestParam("userId") Long userId);

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 退出登录
     * @date 17:50 2019-12-31
     **/
    @GetMapping("/cas/logout/user")
    Boolean casLogout();

    /**
     * @param clientId, clientSecret, refreshToken
     * @return com.seaboxdata.auth.api.dto.domain.Token
     * @author makaiyu
     * @description 根据refreshToken 获取新的accessToken
     * @date 14:47 2019/9/29
     **/
    @GetMapping("/refresh/token/user")
    Token refreshToken(@RequestParam("clientId") String clientId,
                       @RequestParam("clientSecret") String clientSecret,
                       @RequestParam("refreshToken") String refreshToken);


    /**
     * @param addressDTO
     * @return void
     * @author makaiyu
     * @description 保存用户登陆日志
     * @date 14:38 2020-01-02
     **/
    @PostMapping("/save/address/log")
    void saveAddressLog(@RequestBody AddressDTO addressDTO);

    /**
     * @return com.seaboxdata.commons.utils.Resp
     * @author makaiyu
     * @description 登陆
     * @date 10:35 2019/5/20
     **/
    @GetMapping(value = "/test/login/user")
    String testLoginUser(@RequestParam("username") String username,
                         @RequestParam("pwd") String pwd);

}
