package com.seaboxdata.auth.server.service.impl;

import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.server.service.TenantCodeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TenantCodeServiceImplTest extends AuthApplicationTests {

    @Autowired
    private TenantCodeService tenantCodeService;

    @Test
    public void checkActivationCode() {
    }

    @Test
    public void checkTenantCodeStatus() {
    }

    @Test
    public void getTenantCodeByStatus() {
    }

    @Test
    public void selectTenantLevelById() {
       tenantCodeService.selectTenantLevelById(1169875821874253826L);

    }
}