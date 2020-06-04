package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.OauthRegisterTenantDTO;
import com.seaboxdata.auth.api.dto.OauthTenantParamDTO;
import com.seaboxdata.auth.api.dto.OauthTenantStatusDTO;
import com.seaboxdata.auth.api.vo.OauthResultTenantVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 租户信息表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-21
 */
public interface OauthTenantService {

    /**
     * @param tenantUserDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 添加 租户信息 + 用户信息
     * @date 2019/5/28
     **/
    String saveTenantUser(@RequestBody OauthRegisterTenantDTO tenantUserDTO);

    /**
     * @param oauthRegisterTenantDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户ID及name 修改租户及管理员信息
     * @date 16:32 2019/5/28
     **/
    String updateTenantUser(@RequestBody OauthRegisterTenantDTO oauthRegisterTenantDTO);

    /**
     * @return java.util.List<com.seaboxdata.auth.api.dto.OauthResultTenantDTO>
     * @author makaiyu
     * @description 获取全部租户信息
     * @date 10:28 2019/5/29
     */
    List<OauthResultTenantVO> selectAllTenant(@RequestBody(required = false)
                                                      OauthTenantParamDTO oauthTenantParamDTO);

    /**
     * @param oauthTenantStatusDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据tenantIds 修改其启用状态
     * @date 11:02 2019/5/29
     **/
    Boolean updateTenantStatus(@RequestBody OauthTenantStatusDTO oauthTenantStatusDTO);

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验租户可用数量
     * @date 13:46 2019/9/6
     **/
    Boolean checkTenantCount();
}
