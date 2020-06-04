package com.seaboxdata.auth.server.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.dto.TenantLevelDTO;
import com.seaboxdata.auth.api.vo.TenantLevelVO;
import com.seaboxdata.auth.server.service.TenantLevelService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class TenantLevelServiceImplTest extends OauthTenantServiceImplTest {

    @Autowired
    private TenantLevelService tenantLevelService;

    @Test
    public void saveOrUpdateTenantLevel() {

        TenantLevelDTO tenantLevelDTO = new TenantLevelDTO();
        tenantLevelDTO.setStatus(true)
                .setUserCount(2)
                .setTenantLevel(4)
                .setDescription("小鱼级用户");
        tenantLevelService.saveOrUpdateTenantLevel(tenantLevelDTO);
    }

    @Test
    public void deleteTenantLevel() {
        tenantLevelService.deleteTenantLevel(Lists.newArrayList(132078396720156672L, 132078245125427200L));
    }

    @Test
    public void selectTenantLevel() {

        List<TenantLevelVO> tenantLevelVOS = tenantLevelService.selectTenantLevel();

        log.info("result : {} ", tenantLevelVOS);
    }

    @Test
    public void checkTenant() {
        Boolean flag = tenantLevelService.checkTenantLevel();

        log.info("flag : {} ", flag);
    }

}