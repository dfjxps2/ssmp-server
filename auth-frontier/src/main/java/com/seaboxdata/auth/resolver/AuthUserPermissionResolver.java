package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IOauthUserPermissionController;
import com.seaboxdata.auth.api.dto.OauthUserPermissionDTO;
import com.seaboxdata.auth.resolver.input.AuthUserPermissionInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author makaiyu
 * @date 2019/6/6 16:32
 */
@Service
public class AuthUserPermissionResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IOauthUserPermissionController oauthUserPermissionController;

    @Autowired
    public AuthUserPermissionResolver(IOauthUserPermissionController oauthUserPermissionController) {
        this.oauthUserPermissionController = oauthUserPermissionController;
    }

    /**
     * @param authUserPermissionInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据权限码 增加/修改用户权限
     * @date 16:59 2019/5/20
     **/
    public Boolean saveOrUpdateUserPermission(AuthUserPermissionInput authUserPermissionInput) {
        OauthUserPermissionDTO oauthUserPermissionDTO = new OauthUserPermissionDTO();
        BeanUtils.copyProperties(authUserPermissionInput, oauthUserPermissionDTO);
        return oauthUserPermissionController.saveOrUpdateUserPermission(oauthUserPermissionDTO);
    }

}
