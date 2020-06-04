package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.OauthUserPermissionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户-资源许可表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@FeignClient(contextId = "AuthUserPermission",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthUserPermissionController {

    /**
     * @param userId
     * @return java.util.List<com.seaboxdata.auth.model.SysUserPermission>
     * @author makaiyu
     * @description 根据userId 获取用户资源数据
     * @date 15:54 2019/5/13
     **/
    @GetMapping("/userperrmission/get/by/userid")
    List<OauthUserPermissionDTO> getUserPermissionByUserId(@RequestParam("userId") Long userId);

    /**
     * @param oauthUserPermission
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据权限码 增加/修改用户权限
     * @date 16:59 2019/5/20
     **/
    @PostMapping("/userperrmission/save/user")
    Boolean saveOrUpdateUserPermission(@RequestBody OauthUserPermissionDTO oauthUserPermission);


}
