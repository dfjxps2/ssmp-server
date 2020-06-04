package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.utils.domain.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户登陆日志表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-08-09
 */
@FeignClient(contextId = "AuthLoginLog",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthLogUserController {

    /**
    * @author makaiyu
    * @description 发送用户登录消息
    * @date 15:50 2019/8/9
    * @param message
    * @return java.lang.Boolean
    **/
    @PostMapping("/send/login/message")
    Boolean sendLoginMessage(@RequestBody Message message);

}
