package com.seaboxdata.auth.server.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.TenantInfoDTO;
import com.seaboxdata.auth.api.vo.TenantInfoVO;
import com.seaboxdata.auth.server.service.OauthTenantInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class OauthTenantInfoServiceImplTest extends AuthApplicationTests {

    @Autowired
    private OauthTenantInfoService oauthTenantInfoService;

    @Test
    public void saveOrUpdateTenantInfo() {

        TenantInfoDTO tenantInfoDTO = new TenantInfoDTO();
        tenantInfoDTO.setTenantInfoId(1192331222601244674L);
        tenantInfoDTO.setVirtualCurrency(222L);

        Boolean falg = oauthTenantInfoService.saveOrUpdateTenantInfo(Lists.newArrayList());

        log.info("result : {} ", falg);

    }

    @Test
    public void selectTenantInfoByTenantId() {
        TenantInfoDTO tenantInfoDTO = new TenantInfoDTO();
        List<TenantInfoVO> tenantInfoVO = oauthTenantInfoService.selectTenantInfo(tenantInfoDTO);
        log.info("result : {} ", tenantInfoVO);
    }

    @Test
    public void deleteTenantInfoByTenantId() {

        oauthTenantInfoService.deleteTenantInfoByTenantId(Lists.newArrayList(2L, 3L));

    }
}