package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.TenantInfoDTO;
import com.seaboxdata.auth.api.vo.TenantInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 租户额外信息表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-11-07
 */
@FeignClient(contextId = "TenantInfo",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthTenantInfoController {


    /**
     * @param tenantInfoDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存租户信息
     * @date 14:14 2019/11/7
     **/
    @PostMapping("/tenant/info/save/update")
    Boolean saveOrUpdateTenantInfo(@RequestBody List<TenantInfoDTO> tenantInfoDTO);

    /**
     * @param tenantInfoDTO
     * @return com.seaboxdata.auth.api.vo.TenantInfoVO
     * @author makaiyu
     * @description 根据租户Id  查询租户信息
     * @date 14:16 2019/11/7
     **/
    @PostMapping("/select/tenant/info")
    List<TenantInfoVO> selectTenantInfo(@RequestBody TenantInfoDTO tenantInfoDTO);

    /**
     * @param tenantIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户Ids 删除租户信息
     * @date 14:19 2019/11/7
     **/
    @PostMapping("/delete/tenant/info/by/ids")
    Boolean deleteTenantInfoByTenantId(@RequestBody List<Long> tenantIds);

}
