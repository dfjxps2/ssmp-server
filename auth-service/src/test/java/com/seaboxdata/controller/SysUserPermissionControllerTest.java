package com.seaboxdata.controller;

import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.server.model.OauthUserPermission;
import com.seaboxdata.auth.server.service.OauthUserPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class SysUserPermissionControllerTest extends AuthApplicationTests {

    @Autowired
    private OauthUserPermissionService oauthUserPermissionService;

    @Test
    public void getUserPermissionByUserId() {
        List<OauthUserPermission> permission = oauthUserPermissionService.getUserPermissionByUserId(1L);
        log.info("permission : {}", permission);
    }
}