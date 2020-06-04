package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IOauthRoleController;
import com.seaboxdata.auth.api.dto.OauthRoleDTO;
import com.seaboxdata.auth.api.dto.OauthUserRoleDTO;
import com.seaboxdata.auth.api.vo.OauthRoleVO;
import com.seaboxdata.auth.resolver.input.AuthSaveRoleInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author makaiyu
 * @date 2019/7/25 10:48
 */
@Service
public class AuthRoleResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IOauthRoleController oauthRoleController;

    @Autowired
    public AuthRoleResolver(IOauthRoleController oauthRoleController) {
        this.oauthRoleController = oauthRoleController;
    }

    /**
     * @return java.util.List<OauthRole>
     * @author makaiyu
     * @description 获取所有角色列表
     * @date 9:59 2019/5/20
     **/
    public List<OauthRoleVO> selectAllRole() {
        return oauthRoleController.selectAllRole();
    }

    /**
     * @param userId
     * @return java.util.Set<com.seaboxdata.auth.model.SysRole>
     * @author makaiyu
     * @description 根据userId  获取 permissionCodes
     * @date 10:09 2019/5/14
     **/
    public Set<String> selectPermissionCodeByUserId(Long userId) {
        return oauthRoleController.selectPermissionCodeByUserId(userId);
    }

    /**
     * @param authSaveRoleInput
     * @return Boolean
     * @author makaiyu
     * @description 添加角色 同时赋权限
     * @date 18:05 2019/5/27
     **/
    @PreAuthorize("hasAuthority('ucManager')")
    public Boolean saveUpdateOauthRole(AuthSaveRoleInput authSaveRoleInput) {
        OauthRoleDTO oauthRoleDTO = new OauthRoleDTO();
        if (Objects.nonNull(authSaveRoleInput)) {
            BeanUtils.copyProperties(authSaveRoleInput, oauthRoleDTO);
        }
        return oauthRoleController.saveUpdateOauthRole(oauthRoleDTO);
    }

    /**
     * @param roleIds
     * @return Boolean
     * @author makaiyu
     * @description 根据roleIds 删除角色 同时删除中间表
     * @date 18:19 2019/5/27
     **/
    @PreAuthorize("hasAuthority('ucManager')")
    public Boolean deleteOauthRole(List<Long> roleIds) {
        return oauthRoleController.deleteOauthRole(roleIds);
    }

    /**
     * @param oauthUserRoleDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个角色 -> 多个用户
     * @date 14:05 2020-04-26
     **/
    public Boolean saveOrUpdateUserRole(OauthUserRoleDTO oauthUserRoleDTO) {
        return oauthRoleController.saveOrUpdateUserRole(oauthUserRoleDTO);
    }

}
