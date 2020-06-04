package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IOauthTenantController;
import com.seaboxdata.auth.api.dto.OauthRegisterTenantDTO;
import com.seaboxdata.auth.api.dto.OauthTenantParamDTO;
import com.seaboxdata.auth.api.dto.OauthTenantStatusDTO;
import com.seaboxdata.auth.api.vo.OauthResultTenantVO;
import com.seaboxdata.auth.resolver.input.AuthRegisterTenantInput;
import com.seaboxdata.auth.resolver.input.AuthTenantParamInput;
import com.seaboxdata.auth.resolver.input.AuthTenantStatusInput;
import com.seaboxdata.auth.resolver.input.AuthUpdateTenantInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author makaiyu
 * @date 2019/5/29 14:08
 */
@Service
public class AuthTenantResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IOauthTenantController oauthTenantController;

    @Autowired
    public AuthTenantResolver(IOauthTenantController oauthTenantController) {
        this.oauthTenantController = oauthTenantController;
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.dto.OauthResultTenantDTO>
     * @author makaiyu
     * @description 获取全部租户信息
     * @date 10:28 2019/5/29
     **/
    @PreAuthorize("hasAuthority('platformManager')")
    public List<OauthResultTenantVO> selectAllTenant(AuthTenantParamInput authTenantParamInput) {
        OauthTenantParamDTO oauthTenantParamDTO = new OauthTenantParamDTO();
        if (Objects.nonNull(authTenantParamInput)) {
            BeanUtils.copyProperties(authTenantParamInput, oauthTenantParamDTO);
        }
        return oauthTenantController.selectAllTenant(oauthTenantParamDTO);
    }

    /**
     * @param authRegisterTenantInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 添加 租户信息 + 用户信息
     * @date 2019/5/28
     **/
    @PreAuthorize("hasAuthority('platformManager')")
    public String saveTenantUser(AuthRegisterTenantInput authRegisterTenantInput) {
        OauthRegisterTenantDTO oauthRegisterTenantDTO = new OauthRegisterTenantDTO();
        BeanUtils.copyProperties(authRegisterTenantInput, oauthRegisterTenantDTO);
        return oauthTenantController.saveTenantUser(oauthRegisterTenantDTO);
    }

    /**
     * @param authUpdateTenantInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户ID及name 修改租户及管理员信息
     * @date 16:32 2019/5/28
     **/
    @PreAuthorize("hasAuthority('platformManager')")
    public String updateTenantUser(AuthUpdateTenantInput authUpdateTenantInput) {
        OauthRegisterTenantDTO oauthRegisterTenantDTO = new OauthRegisterTenantDTO();
        BeanUtils.copyProperties(authUpdateTenantInput, oauthRegisterTenantDTO);
        return oauthTenantController.updateTenantUser(oauthRegisterTenantDTO);
    }

    /**
     * @param authTenantStatusInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据tenantIds 修改其启用状态
     * @date 11:02 2019/5/29
     **/
    @PreAuthorize("hasAuthority('platformManager')")
    public Boolean updateTenantStatus(AuthTenantStatusInput authTenantStatusInput) {
        OauthTenantStatusDTO oauthTenantStatusDTO = new OauthTenantStatusDTO();
        BeanUtils.copyProperties(authTenantStatusInput, oauthTenantStatusDTO);
        return oauthTenantController.updateTenantStatus(oauthTenantStatusDTO);
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验租户可用数量
     * @date 13:46 2019/9/6
     **/
    public Boolean checkTenantCount() {
        return oauthTenantController.checkTenantCount();
    }

}
