package com.seaboxdata.auth.server.controller;

import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.controller.IOauthRoleController;
import com.seaboxdata.auth.api.dto.OauthRoleDTO;
import com.seaboxdata.auth.api.dto.OauthUserRoleDTO;
import com.seaboxdata.auth.api.enums.RoleEnum;
import com.seaboxdata.auth.api.vo.OauthRoleVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.model.OauthRole;
import com.seaboxdata.auth.server.service.OauthRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Service
public class OauthRoleController implements IOauthRoleController {

    @Autowired
    private OauthRoleService oauthRoleService;

    @Override
    public List<OauthUserVO> selectUserByRoleId(Long userId) {
        return null;
    }

    /**
     * @return java.util.List<OauthRole>
     * @author makaiyu
     * @description 获取所有角色列表
     * @date 9:59 2019/5/20
     **/
    @Override
    public List<OauthRoleVO> selectAllRole() {
        List<OauthRole> oauthRoles = oauthRoleService.selectAllRole();
        List<OauthRoleVO> roleVOS = Lists.newArrayListWithCapacity(oauthRoles.size());
        if (!CollectionUtils.isEmpty(oauthRoles)) {
            oauthRoles.forEach(oauthRole -> {
                OauthRoleVO oauthRoleDTO = new OauthRoleVO();
                BeanUtils.copyProperties(oauthRole, oauthRoleDTO);
                roleVOS.add(oauthRoleDTO);
            });
        }

        return roleVOS;
    }

    /**
     * @param userId
     * @return java.util.Set<com.seaboxdata.auth.model.SysRole>
     * @author makaiyu
     * @description 根据userId  获取 permissionCodes
     * @date 10:09 2019/5/14
     **/
    @Override
    public Set<String> selectPermissionCodeByUserId(Long userId) {
        return oauthRoleService.selectPermissionCodeByUserId(userId);
    }

    /**
     * @param oauthRoleDTO
     * @return Boolean
     * @author makaiyu
     * @description 添加角色 同时赋权限
     * @date 18:05 2019/5/27
     **/
    @Override
    public Boolean saveUpdateOauthRole(@RequestBody OauthRoleDTO oauthRoleDTO) {
        return oauthRoleService.saveUpdateOauthRole(oauthRoleDTO);
    }

    /**
     * @param roleIds
     * @return Boolean
     * @author makaiyu
     * @description 根据roleIds 删除角色 同时删除中间表
     * @date 18:19 2019/5/27
     **/
    @Override
    public Boolean deleteOauthRole(@RequestBody List<Long> roleIds) {
        return oauthRoleService.deleteOauthRole(roleIds);
    }

    /**
     * @param userId, roleEnum
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断用户角色
     * @date 10:51 2019/8/19
     **/
    @Override
    public Boolean decideUserRole(Long userId, @RequestBody RoleEnum roleEnum) {
        return oauthRoleService.decideUserRole(userId, roleEnum);
    }

    /**
     * @param userId, appName
     * @return java.util.Map<java.lang.Long, java.lang.String>
     * @author makaiyu
     * @description 根据用户Id、系统名称 查询其权限码及Id
     * @date 17:17 2019/11/25
     **/
    @Override
    public Map<Long, String> selectPermissionsByUserIdAndSystem(Long userId, String appName) {
        return oauthRoleService.selectPermissionsByUserIdAndSystem(userId, appName);
    }

    /**
     * @param oauthUserRoleDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存/修改用户角色信息
     * @date 14:05 2020-04-26
     **/
    @Override
    public Boolean saveOrUpdateUserRole(@RequestBody OauthUserRoleDTO oauthUserRoleDTO) {
        return oauthRoleService.saveOrUpdateUserRole(oauthUserRoleDTO);
    }
}
