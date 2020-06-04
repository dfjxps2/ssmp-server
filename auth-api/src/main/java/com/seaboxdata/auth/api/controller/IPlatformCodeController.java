package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.vo.PlatformCodeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 平台-激活码 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-09-06
 */
@FeignClient(contextId = "PlatformCode",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IPlatformCodeController {

    /**
     * @param activityCode
     * @return java.lang.Long
     * @author makaiyu
     * @description 校验平台激活码
     * @date 11:25 2019/9/6
     **/
    @PostMapping("/platform/check/code")
    PlatformCodeVO checkPlatformCode(@RequestParam("activityCode") String activityCode);

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断当前平台用户是否已激活
     * @date 11:39 2019/9/6
     **/
    @PostMapping("/platform/check/status")
    Boolean checkPlatformStatus();

}
