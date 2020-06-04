package com.seaboxdata.auth.server.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.OauthRegisterTenantDTO;
import com.seaboxdata.auth.api.vo.OauthResultTenantVO;
import com.seaboxdata.auth.server.service.OauthTenantService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class OauthTenantServiceImplTest extends AuthApplicationTests {

    @Autowired
    private OauthTenantService oauthTenantService;

    @Test
    public void saveTenantUser() {
        OauthRegisterTenantDTO userDTO = new OauthRegisterTenantDTO();
        userDTO.setTenantId(3L)
                .setTenantName("南京银行1")
                .setEmail("baidu@127.com1")
                .setName("李四1")
                .setPassword("1231111")
                .setPhoneNumber("138771239876")
                .setUsername("苹果1")
                .setTenantDesc("新增");
        oauthTenantService.saveTenantUser(userDTO);
    }

    @Test
    public void updateTenantUser() {
        OauthRegisterTenantDTO userDTO = new OauthRegisterTenantDTO();
        userDTO.setTenantId(2L)
                .setTenantName("北京银行")
                .setEmail("baidu@126.com")
                .setName("张四")
                .setPassword("1231234")
                .setPhoneNumber("13788778765")
                .setUsername("很行hen")
                .setTenantDesc("修改");
        oauthTenantService.updateTenantUser(userDTO);
    }

    @Test
    public void selectAllTenant() {
        List<OauthResultTenantVO> tenantDTOS = oauthTenantService.selectAllTenant(null);
        log.info("tenants :{}", tenantDTOS);
    }

    @Test
    public void updateTenantStatus() {
        List<Long> tenantIds = Lists.newArrayList();
        tenantIds.add(1L);
        tenantIds.add(2L);
//        oauthTenantService.updateTenantStatus(tenantIds);
    }
}