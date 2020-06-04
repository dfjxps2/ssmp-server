package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.AppDataOperationDTO;
import com.seaboxdata.auth.api.dto.AppDataParamDTO;
import com.seaboxdata.auth.api.dto.AppDataTypeDTO;
import com.seaboxdata.auth.api.vo.AppDataTypeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-07-26
 */
@FeignClient(contextId = "AppDateType",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IAppDataTypeController {

    /**
     * @param appDataTypeDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.AppDataTypeVO>
     * @author makaiyu
     * @description 获取全部应用数据信息
     * @date 9:38 2019/7/26
     **/
    @PostMapping("/data/type/select/all")
    List<AppDataTypeVO> selectAllDataType(@RequestBody(required = false) AppDataTypeDTO appDataTypeDTO);

    /**
     * @param appDataParamDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 增加或修改应用数据类型
     * @date 9:41 2019/7/26
     **/
    @PostMapping("/data/type/save/update")
    Boolean saveOrUpdateDataType(@RequestBody AppDataParamDTO appDataParamDTO);

    /**
     * @param dataTypeIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除应用数据类型
     * @date 9:42 2019/7/26
     **/
    @PostMapping("/data/type/delete")
    Boolean deleteDateType(@RequestBody List<Long> dataTypeIds);

    /**
     * @param appDataOperationDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 移动应用数据位置
     * @date 9:56 2019/7/26
     **/
    @PostMapping("/data/type/move")
    Boolean operationDataType(@RequestBody AppDataOperationDTO appDataOperationDTO);

}
