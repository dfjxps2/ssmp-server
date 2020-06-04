package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.TenantLevelDTO;
import com.seaboxdata.auth.api.vo.TenantLevelVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 租户级别服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-08-27
 */
@FeignClient(contextId = "TenantLevel",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface ITenantLevelController {

    /**
     * @param tenantLevelDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存/修改租户级别
     * @date 10:45 2019/8/27
     **/
    @PostMapping("/tenant/level/save/update")
    Boolean saveOrUpdateTenantLevel(@RequestBody TenantLevelDTO tenantLevelDTO);

    /**
     * @param tenantLevelIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除租户级别信息
     * @date 10:46 2019/8/27
     **/
    @PostMapping("/tenant/level/delete")
    Boolean deleteTenantLevel(@RequestBody List<Long> tenantLevelIds);

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.TenantLevelVO>
     * @author makaiyu
     * @description 查询全部用户级别信息
     * @date 10:46 2019/8/27
     **/
    @PostMapping("/tenant/level/select/all")
    List<TenantLevelVO> selectTenantLevel();

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 检验当前租户是否满足创建用户的级别
     * @date 11:23 2019/8/27
     **/
    @PostMapping("/tenant/level/check")
    Boolean checkTenantLevel();

}
