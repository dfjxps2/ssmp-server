package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.TenantInfoDTO;
import com.seaboxdata.auth.api.vo.TenantInfoVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 租户额外信息表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-11-07
 */
public interface OauthTenantInfoService {

    /**
     * @param tenantInfoDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存租户信息
     * @date 14:14 2019/11/7
     **/
    Boolean saveOrUpdateTenantInfo(@RequestBody List<TenantInfoDTO> tenantInfoDTO);

    /**
     * @param tenantInfoDTO
     * @return com.seaboxdata.auth.api.vo.TenantInfoVO
     * @author makaiyu
     * @description 根据租户Id  查询租户信息
     * @date 14:16 2019/11/7
     **/
    List<TenantInfoVO> selectTenantInfo(@RequestBody TenantInfoDTO tenantInfoDTO);

    /**
     * @param tenantIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户Ids 删除租户信息
     * @date 14:19 2019/11/7
     **/
    Boolean deleteTenantInfoByTenantId(@RequestBody List<Long> tenantIds);
}
