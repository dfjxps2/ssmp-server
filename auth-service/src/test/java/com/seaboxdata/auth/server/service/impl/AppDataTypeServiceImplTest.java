package com.seaboxdata.auth.server.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.AppDataOperationDTO;
import com.seaboxdata.auth.api.dto.AppDataParamDTO;
import com.seaboxdata.auth.api.enums.JumpEnum;
import com.seaboxdata.auth.api.vo.AppDataTypeVO;
import com.seaboxdata.auth.server.service.AppDataTypeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class AppDataTypeServiceImplTest extends AuthApplicationTests {

    @Autowired
    private AppDataTypeService appDataTypeService;

    @Test
    public void selectAllDataType() {
        List<AppDataTypeVO> appDataTypeVOS = appDataTypeService.selectAllDataType(null);
        log.info("result: {}", appDataTypeVOS);
    }

    @Test
    public void saveOrUpdateDataType() {
        AppDataParamDTO appDataParamDTO = new AppDataParamDTO();
        appDataParamDTO.setDataTypeName("测试12139")
                .setJumpMode(JumpEnum.CURRENTWINDOW)
                .setUrl("ceshi.com")
                .setParentId(0L);
        appDataTypeService.saveOrUpdateDataType(appDataParamDTO);

    }

    @Test
    public void deleteDateType() {
        appDataTypeService.deleteDateType(Lists.newArrayList(120474678040072192L, 120476046339477504L));
    }

    @Test
    public void operationDataType() {
        AppDataOperationDTO appDataOperationDTO = new AppDataOperationDTO();
        appDataOperationDTO.setSourceDataTypeId(4L)
                .setTargetDataTypeId(1L)
                .setMoveOperation(true);
        appDataTypeService.operationDataType(appDataOperationDTO);

    }
}