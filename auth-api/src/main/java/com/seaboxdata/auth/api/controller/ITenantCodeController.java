package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.vo.TenantCodeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 租户-级别-激活码 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-09-03
 */
@FeignClient(contextId = "TenantCode",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface ITenantCodeController {

    /**
     * @param activationCode
     * @return com.seaboxdata.auth.api.vo.TenantCodeVO
     * @author makaiyu
     * @description 校验激活码
     * @date 15:19 2019/9/3
     **/
    @PostMapping("/tenant/check/activation/code")
    TenantCodeVO checkActivationCode(@RequestParam("activationCode") String activationCode);

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验当前登录用户是否存在激活的激活码
     * @date 11:37 2019/9/5
     **/
    @GetMapping("/tenant/code/check/status")
    Boolean checkTenantCodeStatus();

    /**
     * @return java.lang.Integer
     * @author makaiyu
     * @description 获取当前登录用户的租户级别
     * @date 14:15 2019/9/10
     **/
    @GetMapping("/tenant/code/select/level/by/id")
    Integer selectTenantLevelById(@RequestParam("tenantId") Long tenantId);
}
