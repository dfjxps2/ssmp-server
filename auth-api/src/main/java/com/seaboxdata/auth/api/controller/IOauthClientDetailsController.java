package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.OauthClientDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-16
 */
@FeignClient(contextId = "AuthClient",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
public interface IOauthClientDetailsController {

    /**
     * @param clientId
     * @return OauthClientDetails
     * @author makaiyu
     * @description 根据code 获取oauth对象
     * @date 18:02 2019/5/16
     **/
    @GetMapping("/client/select/oauthclient/code")
    OauthClientDetailsDTO selectOauthClientByClientId(@RequestParam("clientId") String clientId);


}
