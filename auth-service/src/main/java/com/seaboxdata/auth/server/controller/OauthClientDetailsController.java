package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthClientDetailsController;
import com.seaboxdata.auth.api.dto.OauthClientDetailsDTO;
import com.seaboxdata.auth.server.service.OauthClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-16
 */
@Service
public class OauthClientDetailsController implements IOauthClientDetailsController {

    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;

    /**
     * @param clientId
     * @return OauthClientDetails
     * @author makaiyu
     * @description 根据code 获取oauth对象
     * @date 18:02 2019/5/16
     **/
    @Override
    public OauthClientDetailsDTO selectOauthClientByClientId(String clientId) {
        return oauthClientDetailsService.selectOauthClientByClientId(clientId);
    }
}
