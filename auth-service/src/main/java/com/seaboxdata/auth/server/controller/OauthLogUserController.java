package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthLogUserController;
import com.seaboxdata.auth.api.utils.domain.Message;
import com.seaboxdata.auth.server.service.OauthLogUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户登陆日志表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-08-09
 */
@Slf4j
@RestController
public class OauthLogUserController implements IOauthLogUserController {

    @Autowired
    private OauthLogUserService oauthLogUserService;

    /**
     * @param message
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 发送用户登录消息
     * @date 15:50 2019/8/9
     **/
    @Override
    public Boolean sendLoginMessage(@RequestBody Message message) {
        return oauthLogUserService.sendLoginMessage(message);
    }
}
