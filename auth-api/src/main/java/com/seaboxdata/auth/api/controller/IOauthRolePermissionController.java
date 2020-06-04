package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.OauthRolePermissionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 角色-资源许可表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@FeignClient(contextId = "AuthRolePermissoin",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
public interface IOauthRolePermissionController {

    /**
     * @param roleId
     * @return java.util.List<com.seaboxdata.auth.model.SysRolePermission>
     * @author makaiyu
     * @description 根据roleIds 获取permissionList
     * @date 18:23 2019/5/13
     **/
    @GetMapping("/permission/select/by/ids")
    List<OauthRolePermissionDTO> selectPermissionByRoleId(@RequestParam("roleId") Long roleId);

}
