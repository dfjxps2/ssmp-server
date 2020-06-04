package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthPermissionController;
import com.seaboxdata.auth.api.dto.OauthPermissionDTO;
import com.seaboxdata.auth.api.dto.OauthRoleParamDTO;
import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.vo.OauthPermissionVO;
import com.seaboxdata.auth.server.service.OauthPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 资源许可表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Service
public class OauthPermissionController implements IOauthPermissionController {

    @Autowired
    private OauthPermissionService oauthPermissionService;

    /**
     * @param oauthSystemDTO
     * @return java.util.List<com.seaboxdata.auth.api.model.OauthPermission>
     * @author makaiyu
     * @description 获取permission列表
     * @date 17:55 2019/5/27
     */
    @Override
    public List<OauthPermissionVO> selectAllPermission(@RequestBody OauthSystemDTO oauthSystemDTO) {
        return oauthPermissionService.selectAllPermission(oauthSystemDTO);
    }

    /**
     * @param oauthRoleParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.dto.OauthPermissionDTO>
     * @author makaiyu
     * @description 根据roleId 获取对应permission
     * @date 17:22 2019/6/3
     **/
    @Override
    public List<OauthPermissionVO> selectPermissionByRoleId(@RequestBody OauthRoleParamDTO oauthRoleParamDTO) {
        return oauthPermissionService.selectPermissionByRoleId(oauthRoleParamDTO);
    }

    /**
     * @param permissionIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据角色添加权限
     * @date 17:35 2019/6/3
     **/
    @Override
    public Boolean saveOrUpdatePermissionByRoleId(@RequestBody List<Long> permissionIds, Long roleId,
                                                  String appName) {
        return oauthPermissionService.saveOrUpdatePermissionByRoleId(permissionIds, roleId, appName);
    }

    /**
     * @param oauthPermissionDTOS
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 批量增加权限许可
     * @date 18:50 2019/6/6
     **/
    @Override
    public Boolean savePermissions(@RequestBody List<OauthPermissionDTO> oauthPermissionDTOS) {
        return oauthPermissionService.savePermissions(oauthPermissionDTOS);
    }

    /**
     * @param permissionCodes
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据权限码获取拥有权限的人
     * @date 15:39 2019/10/28
     **/
    @Override
    public List<Long> selectUserIdByPermissionCodes(@RequestBody List<String> permissionCodes,
                                                    Long tenantId) {
        return oauthPermissionService.selectUserIdByPermissionCodes(permissionCodes, tenantId);
    }

    /**
     * @param permissionIds
     * @return java.util.Map<java.lang.Long, java.lang.Long>
     * @author makaiyu
     * @description 根据权限码Id 查找其父Id
     * @date 17:20 2019/11/25
     **/
    @Override
    public Map<Long, Long> selectPermissionParentByIds(@RequestBody List<Long> permissionIds) {
        return oauthPermissionService.selectPermissionParentByIds(permissionIds);
    }

    /**
     * @param permissionId, parentId,appName
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改permissionId所处父Id
     * @date 17:23 2019/11/25
     **/
    @Override
    public Boolean updatePermissionParentById(Long permissionId, Long parentId, String appName) {
        return oauthPermissionService.updatePermissionParentById(permissionId, parentId, appName);
    }

    /**
     * @param permissionCodes
     * @return String
     * @author makaiyu
     * @description 根据权限码 获取对应系统
     * @date 11:03 2020-03-23
     **/
    @Override
    public Set<String> selectAppKeyByPermissionCodes(Set<String> permissionCodes) {
        return oauthPermissionService.selectAppKeyByPermissionCodes(permissionCodes);
    }


}
