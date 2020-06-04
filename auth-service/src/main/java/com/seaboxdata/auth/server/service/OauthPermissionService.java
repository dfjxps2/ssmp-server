package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.OauthPermissionDTO;
import com.seaboxdata.auth.api.dto.OauthRoleParamDTO;
import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.vo.OauthPermissionVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 资源许可表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
public interface OauthPermissionService {

    /**
     * @param oauthSystemDTO
     * @return java.util.List<com.seaboxdata.auth.api.model.OauthPermission>
     * @author makaiyu
     * @description 获取permission列表
     * @date 17:55 2019/5/27
     */
    List<OauthPermissionVO> selectAllPermission(@RequestBody OauthSystemDTO oauthSystemDTO);

    /**
     * @param oauthRoleParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.dto.OauthPermissionDTO>
     * @author makaiyu
     * @description 根据roleId 获取对应permission
     * @date 17:22 2019/6/3
     **/
    List<OauthPermissionVO> selectPermissionByRoleId(@RequestBody OauthRoleParamDTO oauthRoleParamDTO);

    /**
     * @param permissionIds
     * @param roleId
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据角色添加权限
     * @date 17:35 2019/6/3
     **/
    Boolean saveOrUpdatePermissionByRoleId(@RequestBody List<Long> permissionIds,
                                           @RequestParam("roleId") Long roleId,
                                           @RequestParam("appName") String appName);

    /**
     * @param oauthPermissionDTOS
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 批量增加权限许可
     * @date 18:50 2019/6/6
     **/
    Boolean savePermissions(@RequestBody List<OauthPermissionDTO> oauthPermissionDTOS);

    /**
     * @param permissionCodes
     * @param tenantId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据权限码获取拥有权限的人
     * @date 15:39 2019/10/28
     **/
    List<Long> selectUserIdByPermissionCodes(@RequestBody List<String> permissionCodes, Long tenantId);

    /**
     * @param permissionIds
     * @return java.util.Map<java.lang.Long, java.lang.Long>
     * @author makaiyu
     * @description 根据权限码Id 查找其父Id
     * @date 17:20 2019/11/25
     **/
    Map<Long, Long> selectPermissionParentByIds(@RequestBody List<Long> permissionIds);

    /**
     * @param permissionId, parentId,appName
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改permissionId所处父Id
     * @date 17:23 2019/11/25
     **/
    Boolean updatePermissionParentById(Long permissionId, Long parentId, String appName);

    /**
     * @param permissionCodes
     * @return String
     * @author makaiyu
     * @description 根据权限码 获取对应系统
     * @date 11:03 2020-03-23
     **/
    Set<String> selectAppKeyByPermissionCodes(Set<String> permissionCodes);
}
