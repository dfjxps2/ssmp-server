package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.controller.IOauthTenantInfoController;
import com.seaboxdata.auth.api.dto.TenantInfoDTO;
import com.seaboxdata.auth.api.vo.TenantInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author makaiyu
 * @date 2019/11/7 15:00
 */
@Service
public class AuthTenantInfoResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IOauthTenantInfoController oauthTenantInfoController;

    @Autowired
    public AuthTenantInfoResolver(IOauthTenantInfoController oauthTenantInfoController) {
        this.oauthTenantInfoController = oauthTenantInfoController;
    }

    /**
     * @param tenantInfoDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存租户信息
     * @date 14:14 2019/11/7
     **/
    public Boolean saveOrUpdateTenantInfo(List<TenantInfoDTO> tenantInfoDTO) {
        if (CollectionUtils.isEmpty(tenantInfoDTO)) {
            return false;
        }
        return oauthTenantInfoController.saveOrUpdateTenantInfo(tenantInfoDTO);
    }

    /**
     * @param tenantInfoInput
     * @return com.seaboxdata.auth.api.vo.TenantInfoVO
     * @author makaiyu
     * @description 根据租户Id  查询租户信息
     * @date 14:16 2019/11/7
     **/
    public List<TenantInfoVO> selectTenantInfo(TenantInfoDTO tenantInfoInput) {
        if (Objects.isNull(tenantInfoInput)) {
            return Lists.newArrayList();
        }
        return oauthTenantInfoController.selectTenantInfo(tenantInfoInput);
    }

    /**
     * @param tenantIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户Ids 删除租户信息
     * @date 14:19 2019/11/7
     **/
    public Boolean deleteTenantInfoByTenantId(List<Long> tenantIds) {
        return oauthTenantInfoController.deleteTenantInfoByTenantId(tenantIds);
    }

}
