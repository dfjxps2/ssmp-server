package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.OauthLogUserDTO;
import com.seaboxdata.auth.api.utils.domain.Message;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 用户登陆日志表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-08-09
 */
public interface OauthLogUserService {

    /**
     * @param message
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 发送用户登录消息
     * @date 15:50 2019/8/9
     **/
    Boolean sendLoginMessage(@RequestBody Message message);


    /**
     * @param oauthLogUserDTO
     * @return boolean
     * @author makaiyu
     * @description 保存记录
     * @date 9:33 2019/8/16
     **/
    boolean saveLogUser(@RequestBody OauthLogUserDTO oauthLogUserDTO);
}
