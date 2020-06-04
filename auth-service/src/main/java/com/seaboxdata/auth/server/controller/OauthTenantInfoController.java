package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthTenantInfoController;
import com.seaboxdata.auth.api.dto.TenantInfoDTO;
import com.seaboxdata.auth.api.vo.TenantInfoVO;
import com.seaboxdata.auth.server.service.OauthTenantInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 租户额外信息表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-11-07
 */
@Service
public class OauthTenantInfoController implements IOauthTenantInfoController {

    @Autowired
    private OauthTenantInfoService oauthTenantInfoService;

    /**
     * @param tenantInfoDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存租户信息
     * @date 14:14 2019/11/7
     **/
    @Override
    public Boolean saveOrUpdateTenantInfo(@RequestBody List<TenantInfoDTO> tenantInfoDTO) {
        return oauthTenantInfoService.saveOrUpdateTenantInfo(tenantInfoDTO);
    }

    /**
     * @param tenantInfoDTO
     * @return com.seaboxdata.auth.api.vo.TenantInfoVO
     * @author makaiyu
     * @description 根据租户Id  查询租户信息
     * @date 14:16 2019/11/7
     **/
    @Override
    public List<TenantInfoVO> selectTenantInfo(TenantInfoDTO tenantInfoDTO) {
        return oauthTenantInfoService.selectTenantInfo(tenantInfoDTO);
    }

    /**
     * @param tenantIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户Ids 删除租户信息
     * @date 14:19 2019/11/7
     **/
    @Override
    public Boolean deleteTenantInfoByTenantId(@RequestBody List<Long> tenantIds) {
        return oauthTenantInfoService.deleteTenantInfoByTenantId(tenantIds);
    }
}
