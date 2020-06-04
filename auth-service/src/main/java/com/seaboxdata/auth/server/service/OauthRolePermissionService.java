package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.server.model.OauthRolePermission;
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
public interface OauthRolePermissionService {

    /**
     * @param roleId
     * @return java.util.List<com.seaboxdata.auth.model.SysRolePermission>
     * @author makaiyu
     * @description 根据roleIds 获取permissionList
     * @date 18:23 2019/5/13
     **/
    List<OauthRolePermission> selectPermissionByRoleId(@RequestParam("roleId") Long roleId);

}
