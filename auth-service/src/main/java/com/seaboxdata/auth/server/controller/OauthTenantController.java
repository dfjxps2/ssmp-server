package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthTenantController;
import com.seaboxdata.auth.api.dto.OauthRegisterTenantDTO;
import com.seaboxdata.auth.api.dto.OauthTenantParamDTO;
import com.seaboxdata.auth.api.dto.OauthTenantStatusDTO;
import com.seaboxdata.auth.api.vo.OauthResultTenantVO;
import com.seaboxdata.auth.server.service.OauthTenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 租户信息表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-21
 */
@Service
@Slf4j
public class OauthTenantController implements IOauthTenantController {

    @Autowired
    private OauthTenantService oauthTenantService;

    /**
     * @param tenantUserDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 添加 租户信息 + 用户信息
     * @date 2019/5/28
     **/
    @Override
    public String saveTenantUser(@RequestBody OauthRegisterTenantDTO tenantUserDTO) {
        return oauthTenantService.saveTenantUser(tenantUserDTO);
    }

    /**
     * @param oauthRegisterTenantDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户ID及name 修改租户及管理员信息
     * @date 16:32 2019/5/28
     **/
    @Override
    public String updateTenantUser(@RequestBody OauthRegisterTenantDTO oauthRegisterTenantDTO) {
        return oauthTenantService.updateTenantUser(oauthRegisterTenantDTO);
    }

    /**
     * @param oauthTenantParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.dto.OauthResultTenantDTO>
     * @author makaiyu
     * @description 获取全部租户信息
     * @date 10:28 2019/5/29
     */
    @Override
    public List<OauthResultTenantVO> selectAllTenant(@RequestBody(required = false)
                                                             OauthTenantParamDTO oauthTenantParamDTO) {
        return oauthTenantService.selectAllTenant(oauthTenantParamDTO);
    }

    /**
     * @param oauthTenantStatusDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据tenantIds 修改其启用状态
     * @date 11:02 2019/5/29
     **/
    @Override
    public Boolean updateTenantStatus(@RequestBody OauthTenantStatusDTO oauthTenantStatusDTO) {
        return oauthTenantService.updateTenantStatus(oauthTenantStatusDTO);
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验租户可用数量
     * @date 13:46 2019/9/6
     **/
    @Override
    public Boolean checkTenantCount() {
        return oauthTenantService.checkTenantCount();
    }

}
