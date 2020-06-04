package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IAppDataTypeController;
import com.seaboxdata.auth.api.dto.AppDataOperationDTO;
import com.seaboxdata.auth.api.dto.AppDataParamDTO;
import com.seaboxdata.auth.api.dto.AppDataTypeDTO;
import com.seaboxdata.auth.api.vo.AppDataTypeVO;
import com.seaboxdata.auth.resolver.input.AppDataOperationInput;
import com.seaboxdata.auth.resolver.input.AppDataTypeInput;
import com.seaboxdata.auth.resolver.input.AppDataTypeParamInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author makaiyu
 * @date 2019/7/26 11:22
 */
@Service
public class AppDataTypeResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IAppDataTypeController appDataTypeController;

    @Autowired
    public AppDataTypeResolver(IAppDataTypeController appDataTypeController) {
        this.appDataTypeController = appDataTypeController;
    }

    /**
     * @param appDataTypeInput
     * @return java.util.List<com.seaboxdata.auth.api.vo.AppDataTypeVO>
     * @author makaiyu
     * @description 获取全部应用数据信息
     * @date 9:38 2019/7/26
     **/
    public List<AppDataTypeVO> selectAllDataType(AppDataTypeInput appDataTypeInput) {
        AppDataTypeDTO appDataTypeDTO = new AppDataTypeDTO();
        if (Objects.nonNull(appDataTypeInput)) {
            BeanUtils.copyProperties(appDataTypeInput, appDataTypeDTO);
        }

        return appDataTypeController.selectAllDataType(appDataTypeDTO).stream().sorted(
                Comparator.comparing(AppDataTypeVO::getOrderNumber)).collect(Collectors.toList());

//        return appDataTypeController.selectAllDataType(appDataTypeDTO);
    }

    /**
     * @param appDataTypeParamInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 增加或修改应用数据类型
     * @date 9:41 2019/7/26
     **/
    public Boolean saveOrUpdateDataType(AppDataTypeParamInput appDataTypeParamInput) {
        AppDataParamDTO appDataParamDTO = new AppDataParamDTO();
        if (Objects.nonNull(appDataTypeParamInput)) {
            BeanUtils.copyProperties(appDataTypeParamInput, appDataParamDTO);
        }
        return appDataTypeController.saveOrUpdateDataType(appDataParamDTO);
    }

    /**
     * @param dataTypeIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除应用数据类型
     * @date 9:42 2019/7/26
     **/
    public Boolean deleteDateType(List<Long> dataTypeIds) {
        return appDataTypeController.deleteDateType(dataTypeIds);
    }

    /**
     * @param appDataOperationInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 移动应用数据位置
     * @date 9:56 2019/7/26
     **/
    public Boolean operationDataType(AppDataOperationInput appDataOperationInput) {
        AppDataOperationDTO appDataOperationDTO = new AppDataOperationDTO();
        if (Objects.nonNull(appDataOperationInput)) {
            BeanUtils.copyProperties(appDataOperationInput, appDataOperationDTO);
        }
        return appDataTypeController.operationDataType(appDataOperationDTO);
    }
}
