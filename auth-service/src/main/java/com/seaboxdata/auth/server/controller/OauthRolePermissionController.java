package com.seaboxdata.auth.server.controller;

import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.controller.IOauthRolePermissionController;
import com.seaboxdata.auth.api.dto.OauthRolePermissionDTO;
import com.seaboxdata.auth.server.model.OauthRolePermission;
import com.seaboxdata.auth.server.service.OauthRolePermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 角色-资源许可表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Service
public class OauthRolePermissionController implements IOauthRolePermissionController {

    @Autowired
    private OauthRolePermissionService oauthRolePermissionService;

    /**
     * @param roleId
     * @return java.util.List<com.seaboxdata.auth.model.SysRolePermission>
     * @author makaiyu
     * @description 根据roleIds 获取permissionList
     * @date 18:23 2019/5/13
     **/
    @Override
    public List<OauthRolePermissionDTO> selectPermissionByRoleId(Long roleId) {
        List<OauthRolePermission> oauthRolePermissions = oauthRolePermissionService.selectPermissionByRoleId(roleId);
        List<OauthRolePermissionDTO> rolePermissionDTOS = Lists.newArrayListWithCapacity(oauthRolePermissions.size());
        if (!CollectionUtils.isEmpty(oauthRolePermissions)) {
            oauthRolePermissions.forEach(oauthRolePermission -> {
                OauthRolePermissionDTO oauthRolePermissionDTO = new OauthRolePermissionDTO();
                BeanUtils.copyProperties(oauthRolePermission, oauthRolePermissionDTO);
                rolePermissionDTOS.add(oauthRolePermissionDTO);
            });
        }
        return rolePermissionDTOS;
    }
}
