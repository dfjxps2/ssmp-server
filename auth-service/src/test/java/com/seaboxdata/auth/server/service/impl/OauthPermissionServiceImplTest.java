package com.seaboxdata.auth.server.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.OauthPermissionDTO;
import com.seaboxdata.auth.api.dto.OauthRoleParamDTO;
import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.vo.OauthPermissionVO;
import com.seaboxdata.auth.server.service.OauthPermissionService;
import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

@Slf4j
@ActiveProfiles("zyc")
public class OauthPermissionServiceImplTest extends AuthApplicationTests {

    @Autowired
    private OauthPermissionService oauthPermissionService;

    @Test
    public void selectAllPermission() {
        OauthSystemDTO oauthSystemDTO = new OauthSystemDTO();
        oauthSystemDTO.setAppName(AppKeyEnum.MDS);
        List<OauthPermissionVO> oauthPermissions = oauthPermissionService.selectAllPermission(oauthSystemDTO);
        log.info("result : {} ", oauthPermissions);
    }

    @Test
    public void selectPermissionByRoleId() {

        OauthRoleParamDTO oauthRoleParamDTO = new OauthRoleParamDTO();
        oauthRoleParamDTO.setRoleId(1L);
        List<OauthPermissionVO> oauthPermissionDTOS =
                oauthPermissionService.selectPermissionByRoleId(oauthRoleParamDTO);
        log.info("permission: {}", oauthPermissionDTOS);
    }

    @Test
    public void savePermissionByRoleId() {
        List<Long> permissionIds = Lists.newArrayList(2L, 3L);
        Boolean flag = oauthPermissionService.saveOrUpdatePermissionByRoleId(permissionIds,
                4L, "MDS");
        log.info("flag :{}", flag);
    }

    @Test
    public void savePermissions() {
        List<OauthPermissionDTO> oauthPermissions = Lists.newArrayList();

        OauthPermissionDTO oauthPermission = new OauthPermissionDTO();
        oauthPermission.setPermissionCode("jxpm_customerPanorama_pic")
                .setDescription("全景图")
                .setPermissionName("全景图")
                .setAppName(AppKeyEnum.JXPM)
                .setParentId(227384941208539136L);
        oauthPermissions.add(oauthPermission);










//        oauthPermission.setPermissionCode("skl_dataResource")
//                .setDescription("数据源")
//                .setPermissionName("数据源")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652369371136L);
//
//        OauthPermissionDTO oauthPermission1 = new OauthPermissionDTO();
//        oauthPermission1.setPermissionCode("skl_dataCond")
//                .setDescription("数据标准条件")
//                .setPermissionName("数据标准条件")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652369371136L);
//
//        OauthPermissionDTO oauthPermission2 = new OauthPermissionDTO();
//        oauthPermission2.setPermissionCode("skl_property")
//                .setDescription("属性")
//                .setPermissionName("属性")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652369371136L);
//
//        OauthPermissionDTO oauthPermission3 = new OauthPermissionDTO();
//        oauthPermission3.setPermissionCode("skl_model")
//                .setDescription("模板")
//                .setPermissionName("模板")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652369371136L);
//
//        OauthPermissionDTO oauthPermission4 = new OauthPermissionDTO();
//        oauthPermission4.setPermissionCode("skl_slaEmail")
//                .setDescription("SLA电子邮件")
//                .setPermissionName("SLA电子邮件")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652369371136L);
//
//        OauthPermissionDTO oauthPermission5 = new OauthPermissionDTO();
//        oauthPermission5.setPermissionCode("skl_user")
//                .setDescription("用户")
//                .setPermissionName("用户")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652369371136L);
//
//        OauthPermissionDTO oauthPermission6 = new OauthPermissionDTO();
//
//        oauthPermission6.setPermissionCode("skl_group")
//                .setDescription("组")
//                .setPermissionName("组")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652369371136L);
//
//        OauthPermissionDTO oauthPermission7 = new OauthPermissionDTO();
//
//        oauthPermission7.setPermissionCode("skl_dataTask")
//                .setDescription("数据任务")
//                .setPermissionName("数据任务")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652302262272L);
//
//        OauthPermissionDTO oauthPermission8 = new OauthPermissionDTO();
//
//        oauthPermission8.setPermissionCode("skl_classFiler")
//                .setDescription("分类")
//                .setPermissionName("分类")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652302262272L);
//
//        OauthPermissionDTO oauthPermission9 = new OauthPermissionDTO();
//
//        oauthPermission9.setPermissionCode("skl_sla")
//                .setDescription("SLA")
//                .setPermissionName("SLA")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652302262272L);
//
//        OauthPermissionDTO oauthPermission10 = new OauthPermissionDTO();
//
//        oauthPermission10.setPermissionCode("skl_viewSearch")
//                .setDescription("可视化查询")
//                .setPermissionName("可视化查询")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652302262272L);
//
//
//        OauthPermissionDTO oauthPermission11 = new OauthPermissionDTO();
//
//        oauthPermission11.setPermissionCode("skl_dataList")
//                .setDescription("数据目录")
//                .setPermissionName("数据目录")
//                .setAppName(AppKeyEnum.DATATRANS)
//                .setParentId(220167652302262272L);


//        oauthPermissions.add(oauthPermission);
//        oauthPermissions.add(oauthPermission1);
//        oauthPermissions.add(oauthPermission2);
//        oauthPermissions.add(oauthPermission3);
//        oauthPermissions.add(oauthPermission4);
//        oauthPermissions.add(oauthPermission5);
//        oauthPermissions.add(oauthPermission6);
//        oauthPermissions.add(oauthPermission7);
//        oauthPermissions.add(oauthPermission8);
//        oauthPermissions.add(oauthPermission9);
//        oauthPermissions.add(oauthPermission10);
//        oauthPermissions.add(oauthPermission11);


        oauthPermissionService.savePermissions(oauthPermissions);
    }

    @Test
    public void selectUserIdByPermissionCodes() {
        List<Long> d4aIsAdmin = oauthPermissionService.selectUserIdByPermissionCodes
                (Lists.newArrayList("dqsIssueApproval"), 1L);

        log.info("result :{} ", d4aIsAdmin);
    }

    @Test
    public void selectPermissionParentByIds() {
        Map<Long, Long> result = oauthPermissionService.selectPermissionParentByIds(Lists.newArrayList(130340104815382528L, 130340519166480384L));
        log.info("result : {} ", result);
    }

    @Test
    public void updatePermissionParentById() {
        oauthPermissionService.updatePermissionParentById(130340519166480384L, 130340104484032512L, "DQS");
    }

}