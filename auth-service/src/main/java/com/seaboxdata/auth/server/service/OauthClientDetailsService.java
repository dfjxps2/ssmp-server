package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.OauthClientDetailsDTO;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-16
 */
public interface OauthClientDetailsService {

    /**
     * @param clientId
     * @return OauthClientDetails
     * @author makaiyu
     * @description 根据code 获取oauth对象
     * @date 18:02 2019/5/16
     **/
    OauthClientDetailsDTO selectOauthClientByClientId(@RequestParam("clientId") String clientId);
}
