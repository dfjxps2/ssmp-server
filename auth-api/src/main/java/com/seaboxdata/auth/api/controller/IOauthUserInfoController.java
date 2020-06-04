package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;

/**
 * <p>
 * 用户信息-扩展表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
@FeignClient(contextId = "AuthUserInfo",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
public interface IOauthUserInfoController {

}
