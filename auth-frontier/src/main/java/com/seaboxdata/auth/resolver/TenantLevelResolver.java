package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.ITenantLevelController;
import com.seaboxdata.auth.api.dto.TenantLevelDTO;
import com.seaboxdata.auth.api.vo.TenantLevelVO;
import com.seaboxdata.auth.resolver.input.TenantLevelInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author makaiyu
 * @date 2019/8/27 14:20
 */
@Service
public class TenantLevelResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private ITenantLevelController tenantLevelService;

    @Autowired
    public TenantLevelResolver(ITenantLevelController tenantLevelService) {
        this.tenantLevelService = tenantLevelService;
    }

    /**
     * @param tenantLevelInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存/修改租户级别
     * @date 10:45 2019/8/27
     **/
    public Boolean saveOrUpdateTenantLevel(@RequestBody TenantLevelInput tenantLevelInput) {
        TenantLevelDTO tenantLevelDTO = new TenantLevelDTO();
        BeanUtils.copyProperties(tenantLevelInput, tenantLevelDTO);
        return tenantLevelService.saveOrUpdateTenantLevel(tenantLevelDTO);
    }

    /**
     * @param tenantLevelIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除租户级别信息
     * @date 10:46 2019/8/27
     **/
    public Boolean deleteTenantLevel(@RequestBody List<Long> tenantLevelIds) {
        return tenantLevelService.deleteTenantLevel(tenantLevelIds);
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.TenantLevelVO>
     * @author makaiyu
     * @description 查询全部用户级别信息
     * @date 10:46 2019/8/27
     **/
    public List<TenantLevelVO> selectTenantLevel() {
        return tenantLevelService.selectTenantLevel();
    }

    /**
     * @param tenantId
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 检验当前租户是否满足创建用户的级别
     * @date 11:23 2019/8/27
     **/
    public Boolean checkTenantLevel() {
        return tenantLevelService.checkTenantLevel();
    }


}
