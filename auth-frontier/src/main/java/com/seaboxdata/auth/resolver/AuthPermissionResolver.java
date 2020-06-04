package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.controller.IOauthPermissionController;
import com.seaboxdata.auth.api.dto.OauthPermissionDTO;
import com.seaboxdata.auth.api.dto.OauthRoleParamDTO;
import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthPermissionVO;
import com.seaboxdata.auth.resolver.input.AuthPermissionInput;
import com.seaboxdata.auth.resolver.input.AuthPermissionSaveInput;
import com.seaboxdata.auth.resolver.input.AuthRoleIdInput;
import com.seaboxdata.auth.resolver.input.AuthSystemInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author makaiyu
 * @date 2019/6/3 18:11
 */
@Service
public class AuthPermissionResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    private IOauthPermissionController oauthPermissionController;

    @Autowired
    public AuthPermissionResolver(IOauthPermissionController oauthPermissionController) {
        this.oauthPermissionController = oauthPermissionController;
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.model.OauthPermission>
     * @author makaiyu
     * @description 获取permission列表
     * @date 17:55 2019/5/27
     **/
    public List<OauthPermissionVO> selectAllPermission(AuthSystemInput authSystemInput) {
        OauthSystemDTO oauthSystemDTO = new OauthSystemDTO();
        if (Objects.nonNull(authSystemInput)) {
            BeanUtils.copyProperties(authSystemInput, oauthSystemDTO);
        }
        return oauthPermissionController.selectAllPermission(oauthSystemDTO);
    }

    /**
     * @param authRoleIdInput
     * @return java.util.List<com.seaboxdata.auth.api.dto.OauthPermissionDTO>
     * @author makaiyu
     * @description 根据roleId 获取对应permission
     * @date 17:22 2019/6/3
     **/
    public List<OauthPermissionVO> selectPermissionByRoleId(AuthRoleIdInput authRoleIdInput) {
        OauthRoleParamDTO oauthRoleParamDTO = new OauthRoleParamDTO();
        BeanUtils.copyProperties(authRoleIdInput, oauthRoleParamDTO);
        return oauthPermissionController.selectPermissionByRoleId(oauthRoleParamDTO);
    }

    /**
     * @param authPermissionSaveInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据角色添加权限
     * @date 17:35 2019/6/3
     **/
    public Boolean saveOrUpdatePermissionByRoleId(AuthPermissionSaveInput authPermissionSaveInput) {
        return oauthPermissionController.
                saveOrUpdatePermissionByRoleId(authPermissionSaveInput.getPermissionIds(),
                        authPermissionSaveInput.getRoleId(),
                        authPermissionSaveInput.getAppName().toString());
    }

    /**
     * @param authPermissionInputs
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 批量增加权限许可
     * @date 18:50 2019/6/6
     **/
    public Boolean savePermissions(List<AuthPermissionInput> authPermissionInputs) {
        List<OauthPermissionDTO> oauthPermissionDTOS = Lists.newArrayListWithCapacity(authPermissionInputs.size());
        authPermissionInputs.forEach(authPermissionInput -> {
            OauthPermissionDTO oauthPermissionDTO = new OauthPermissionDTO();
            BeanUtils.copyProperties(authPermissionInput, oauthPermissionDTO);
            oauthPermissionDTOS.add(oauthPermissionDTO);
        });

        return oauthPermissionController.savePermissions(oauthPermissionDTOS);
    }


    /**
     * @param permissionCodes
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据权限码获取拥有权限的人
     * @date 15:39 2019/10/28
     **/
    public List<Long> selectUserIdByPermissionCodes(List<String> permissionCodes) {
        return oauthPermissionController.selectUserIdByPermissionCodes(
                permissionCodes, UserUtils.getUserDetails().getTenantId());
    }

}


