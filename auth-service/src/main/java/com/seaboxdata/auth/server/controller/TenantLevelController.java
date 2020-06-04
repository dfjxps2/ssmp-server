package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.ITenantLevelController;
import com.seaboxdata.auth.api.dto.TenantLevelDTO;
import com.seaboxdata.auth.api.vo.TenantLevelVO;
import com.seaboxdata.auth.server.service.TenantLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-08-27
 */
@Service
public class TenantLevelController implements ITenantLevelController {

    @Autowired
    private TenantLevelService tenantLevelService;

    /**
     * @param tenantLevelDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存/修改租户级别
     * @date 10:45 2019/8/27
     **/
    @Override
    public Boolean saveOrUpdateTenantLevel(@RequestBody TenantLevelDTO tenantLevelDTO) {
        return tenantLevelService.saveOrUpdateTenantLevel(tenantLevelDTO);
    }

    /**
     * @param tenantLevelIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除租户级别信息
     * @date 10:46 2019/8/27
     **/
    @Override
    public Boolean deleteTenantLevel(@RequestBody List<Long> tenantLevelIds) {
        return tenantLevelService.deleteTenantLevel(tenantLevelIds);
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.TenantLevelVO>
     * @author makaiyu
     * @description 查询全部用户级别信息
     * @date 10:46 2019/8/27
     **/
    @Override
    public List<TenantLevelVO> selectTenantLevel() {
        return tenantLevelService.selectTenantLevel();
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 检验当前租户是否满足创建用户的级别
     * @date 11:23 2019/8/27
     **/
    @Override
    public Boolean checkTenantLevel() {
        return tenantLevelService.checkTenantLevel();
    }
}
