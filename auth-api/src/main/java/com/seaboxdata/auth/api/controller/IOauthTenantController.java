package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.OauthRegisterTenantDTO;
import com.seaboxdata.auth.api.dto.OauthTenantParamDTO;
import com.seaboxdata.auth.api.dto.OauthTenantStatusDTO;
import com.seaboxdata.auth.api.vo.OauthResultTenantVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 租户信息表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-21
 */
@FeignClient(contextId = "TenantUser",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthTenantController {

    /**
     * @param tenantUserDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 添加 租户信息 + 用户信息
     * @date 2019/5/28
     **/
    @PostMapping("/tenant/save/update/user")
    String saveTenantUser(@RequestBody OauthRegisterTenantDTO tenantUserDTO);

    /**
     * @param oauthRegisterTenantDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户ID及name 修改租户及管理员信息
     * @date 16:32 2019/5/28
     **/
    @PostMapping("/tenant/update/user")
    String updateTenantUser(@RequestBody OauthRegisterTenantDTO oauthRegisterTenantDTO);

    /**
     * @param oauthTenantParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.dto.OauthResultTenantDTO>
     * @author makaiyu
     * @description 获取全部租户信息
     * @date 10:28 2019/5/29
     */
    @PostMapping("/tenant/select/all")
    List<OauthResultTenantVO> selectAllTenant(@RequestBody(required = false)
                                                      OauthTenantParamDTO oauthTenantParamDTO);

    /**
     * @param oauthTenantStatusDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据tenantIds 修改其启用状态
     * @date 11:02 2019/5/29
     **/
    @PostMapping("/tenant/update/status")
    Boolean updateTenantStatus(@RequestBody OauthTenantStatusDTO oauthTenantStatusDTO);

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验租户可用数量
     * @date 13:46 2019/9/6
     **/
    @GetMapping("/tenant/check/count")
    Boolean checkTenantCount();

}
