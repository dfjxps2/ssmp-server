package com.seaboxdata.auth.server.service.impl;

import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.OauthUserPermissionDTO;
import com.seaboxdata.auth.server.service.OauthUserPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class OauthUserPermissionServiceImplTest extends AuthApplicationTests {

    @Autowired
    private OauthUserPermissionService oauthUserPermissionService;

    @Test
    public void getUserPermissionByUserId() {
    }

    @Test
    public void saveUserPermission() {

        OauthUserPermissionDTO permissionDTO = new OauthUserPermissionDTO();
        permissionDTO.setUserId(96257150615162880L);
        permissionDTO.setPermissionIds(Lists.newArrayList(3L));
        permissionDTO.setOperatorId(1L);

        oauthUserPermissionService.saveOrUpdateUserPermission(permissionDTO);

    }
}