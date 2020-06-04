package com.seaboxdata.auth.server.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.OauthRoleDTO;
import com.seaboxdata.auth.api.enums.RoleEnum;
import com.seaboxdata.auth.server.model.OauthRole;
import com.seaboxdata.auth.server.service.OauthRoleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class OauthRoleServiceImplTest extends AuthApplicationTests {

    @Autowired
    private OauthRoleService oauthRoleService;

    @Test
    public void selectRoleByUserId() {
        Set<String> permissionCodes = oauthRoleService.selectPermissionCodeByUserId(1L);

        log.info("permissionCodes : {} ", permissionCodes);
    }

    @Test
    public void selectPermissionCodeByUserId() {
        Set<String> result = oauthRoleService.selectPermissionCodeByUserId(1L);
        log.info("result : {} ", result);
    }

    @Test
    public void selectAllRole() {
        List<OauthRole> oauthRoles = oauthRoleService.selectAllRole();
        log.info("roles: {} ", oauthRoles);
    }

    @Test
    public void saveOauthRole() {
        OauthRoleDTO oauthRoleDTO = new OauthRoleDTO();
        List<Long> permissionIds = Lists.newArrayList();
        permissionIds.add(3L);
        oauthRoleDTO.setPermissionsIds(permissionIds)
                .setId(98851165084717056L)
                .setDescription("小爷12")
                .setRoleCode("XIAOY1E2")
                .setRoleName("1小爷12")
                .setStatus(0)
                .setTenantId(1L);
        oauthRoleService.saveUpdateOauthRole(oauthRoleDTO);
    }

    @Test
    public void deleteOauthRoles() {
        List<Long> roleIds = Lists.newArrayList();
        roleIds.add(98851165084717056L);
        oauthRoleService.deleteOauthRole(roleIds);
    }

    @Test
    public void decideUserRole() {
        Boolean flag = oauthRoleService.decideUserRole(1L, RoleEnum.SYSMANAGER);
        log.info("result : {} ", flag);
    }

    @Test
    public void selectPermissionsByUserIdAndSystem() {
        Map<Long, String> uc = oauthRoleService
                .selectPermissionsByUserIdAndSystem(1L, "DQS");

        log.info("result : {} ", uc);

    }

}