package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.AppDataOperationDTO;
import com.seaboxdata.auth.api.dto.AppDataParamDTO;
import com.seaboxdata.auth.api.dto.AppDataTypeDTO;
import com.seaboxdata.auth.api.vo.AppDataTypeVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-07-26
 */
public interface AppDataTypeService {

    /**
     * @param appDataTypeDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.AppDataTypeVO>
     * @author makaiyu
     * @description 获取全部应用数据信息
     * @date 9:38 2019/7/26
     **/
    List<AppDataTypeVO> selectAllDataType(@RequestBody(required = false) AppDataTypeDTO appDataTypeDTO);


    /**
     * @param appDataParamDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 增加或修改应用数据类型
     * @date 9:41 2019/7/26
     **/
    Boolean saveOrUpdateDataType(@RequestBody AppDataParamDTO appDataParamDTO);

    /**
     * @param dataTypeIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除应用数据类型
     * @date 9:42 2019/7/26
     **/
    Boolean deleteDateType(@RequestBody List<Long> dataTypeIds);

    /**
     * @param appDataOperationDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 移动应用数据位置
     * @date 9:56 2019/7/26
     **/
    Boolean operationDataType(@RequestBody AppDataOperationDTO appDataOperationDTO);
}
