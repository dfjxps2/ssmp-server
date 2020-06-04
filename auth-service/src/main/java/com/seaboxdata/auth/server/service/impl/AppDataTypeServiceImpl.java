package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.dto.AppDataOperationDTO;
import com.seaboxdata.auth.api.dto.AppDataParamDTO;
import com.seaboxdata.auth.api.dto.AppDataTypeDTO;
import com.seaboxdata.auth.api.vo.AppDataTypeVO;
import com.seaboxdata.auth.server.dao.AppDataTypeMapper;
import com.seaboxdata.auth.server.model.AppDataType;
import com.seaboxdata.auth.server.service.AppDataTypeService;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-07-26
 */
@Service
@Slf4j
public class AppDataTypeServiceImpl implements AppDataTypeService {

    @Autowired
    private AppDataTypeMapper appDataTypeMapper;

    /**
     * @param appDataTypeDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.AppDataTypeVO>
     * @author makaiyu
     * @description 获取全部应用数据信息
     * @date 9:38 2019/7/26
     **/
    @Override
    public List<AppDataTypeVO> selectAllDataType(@RequestBody AppDataTypeDTO appDataTypeDTO) {

        LambdaQueryWrapper<AppDataType> wrapper = new QueryWrapper<AppDataType>().lambda();

        if (Objects.nonNull(appDataTypeDTO)) {
            if (Objects.nonNull(appDataTypeDTO.getDataTypeId())) {
                wrapper.eq(AppDataType::getId, appDataTypeDTO.getDataTypeId());
            }

            if (Objects.nonNull(appDataTypeDTO.getKeyWords())) {
                wrapper.like(AppDataType::getDataTypeName, appDataTypeDTO.getKeyWords());
            }
        }

        List<AppDataType> dataTypes = MapperUtils.list(appDataTypeMapper, wrapper);

        List<AppDataTypeVO> appDataTypeVOS = Lists.newArrayListWithCapacity(dataTypes.size());

        if (!CollectionUtils.isEmpty(dataTypes)) {
            dataTypes.forEach(dataType -> {
                AppDataTypeVO appDataTypeVO = new AppDataTypeVO();
                BeanUtils.copyProperties(dataType, appDataTypeVO);
                appDataTypeVO.setDataTypeId(dataType.getId());
                appDataTypeVOS.add(appDataTypeVO);
            });
        }

        return appDataTypeVOS;
    }

    /**
     * @param appDataParamDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 增加或修改应用数据类型
     * @date 9:41 2019/7/26
     **/
    @Override
    public Boolean saveOrUpdateDataType(@RequestBody AppDataParamDTO appDataParamDTO) {

        if (Objects.isNull(appDataParamDTO)) {
            log.warn("saveOrUpdateDataType -> param is null");
            return false;
        }

        AppDataType appDataType = new AppDataType();
        BeanUtils.copyProperties(appDataParamDTO, appDataType);
        appDataType.setJumpMode(String.valueOf(appDataParamDTO.getJumpMode()));

        try {
            if (Objects.nonNull(appDataParamDTO.getDataTypeId())) {
                appDataType.setId(appDataParamDTO.getDataTypeId());
                appDataType.setUpdateTime(LocalDateTime.now());
                MapperUtils.updateById(appDataTypeMapper, appDataType);
            } else {
                MapperUtils.save(appDataTypeMapper, appDataType);
            }
        } catch (Exception e) {
            log.warn("saveOrUpdateDataType -> 添加/修改应用数据信息失败");
            return false;
        }

        return true;
    }

    /**
     * @param dataTypeIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除应用数据类型
     * @date 9:42 2019/7/26
     **/
    @Override
    public Boolean deleteDateType(@RequestBody List<Long> dataTypeIds) {
        if (CollectionUtils.isEmpty(dataTypeIds)) {
            log.warn("deleteDateType -> param dataTypeIds is null");
            return false;
        }

        return MapperUtils.removeByIds(appDataTypeMapper, dataTypeIds);
    }

    /**
     * @param appDataOperationDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 移动应用数据位置
     * @date 9:56 2019/7/26
     **/
    @Override
    public Boolean operationDataType(@RequestBody AppDataOperationDTO appDataOperationDTO) {

        if (Objects.isNull(appDataOperationDTO)) {
            log.warn("appDataOperationDTO -> param is null");
            return false;
        }

        // 1. 获取操作Id 对象
        LambdaQueryWrapper<AppDataType> sourceWrapper = new QueryWrapper<AppDataType>().lambda()
                .eq(AppDataType::getId, appDataOperationDTO.getSourceDataTypeId());

        AppDataType sourceData = MapperUtils.getOne(appDataTypeMapper, sourceWrapper, true);

        // 操作对象页面级别
        Integer sourceDataLevel = sourceData.getLevel();

        // 2. 获取目标Id 对象
        LambdaQueryWrapper<AppDataType> targetWrapper = new QueryWrapper<AppDataType>().lambda()
                .eq(AppDataType::getId, appDataOperationDTO.getTargetDataTypeId());

        AppDataType targetData = MapperUtils.getOne(appDataTypeMapper, targetWrapper, true);

        // 目标对象页面级别
        Integer targetDataLevel = targetData.getLevel();

        if (!sourceDataLevel.equals(targetDataLevel)) {
            log.warn("调整级别不一致 sourceDataLevel : {} , targetDataLevel : {}", sourceDataLevel, targetDataLevel);
            return false;
        }

        // 3. 获取目标对象组内数据对象
        LambdaQueryWrapper<AppDataType> parentWrapper = new QueryWrapper<AppDataType>().lambda()
                .eq(AppDataType::getParentId, sourceData.getParentId());

        // 4. 获取操作类型  修改两个对象
        if (appDataOperationDTO.getMoveOperation()) {
            parentWrapper.between(AppDataType::getOrderNumber, targetData.getOrderNumber(), sourceData.getOrderNumber() - 1);

            // 获取根据orderNumber排序的parentId相同的子集的数据 并对原对象赋值
            List<AppDataType> dataTypes = getAppDataTypes(sourceData, targetData, parentWrapper);

            // 对目标对象与原对象之间的数据 进行orderNumber+1操作
            dataTypes.forEach(appDataType -> {
                appDataType.setOrderNumber(appDataType.getOrderNumber() + 1);
                MapperUtils.updateById(appDataTypeMapper, appDataType);
            });
        } else {
            parentWrapper.between(AppDataType::getOrderNumber, sourceData.getOrderNumber() + 1, targetData.getOrderNumber());

            // 获取根据orderNumber排序的parentId相同的子集的数据 并对原对象赋值
            List<AppDataType> dataTypes = getAppDataTypes(sourceData, targetData, parentWrapper);

            // 对目标对象与原对象之间的数据 进行orderNumber+1操作
            dataTypes.forEach(appDataType -> {
                appDataType.setOrderNumber(appDataType.getOrderNumber() - 1);
                MapperUtils.updateById(appDataTypeMapper, appDataType);
            });
        }

        return true;
    }

    /**
     * @param sourceData, targetData, parentWrapper
     * @return java.util.List<com.seaboxdata.auth.server.model.AppDataType>
     * @author makaiyu
     * @description 获取根据orderNumber排序的parentId相同的子集的数据 并对原对象赋值
     * @date 16:12 2019/7/26
     **/
    private List<AppDataType> getAppDataTypes(AppDataType sourceData, AppDataType targetData, LambdaQueryWrapper<AppDataType> parentWrapper) {
        List<AppDataType> appDataTypes = MapperUtils.list(appDataTypeMapper, parentWrapper);

        // 获取根据orderNumber排序的parentId相同的子集 同时满足orderNumber在原对象-1与目标对象之间的数据
        List<AppDataType> dataTypes = appDataTypes.stream().sorted(
                Comparator.comparing(AppDataType::getOrderNumber)).collect(Collectors.toList());

        // 获取目标对象orderNumber
        Integer targetDataOrderNumber = targetData.getOrderNumber();
        // 为原对象赋值
        sourceData.setOrderNumber(targetDataOrderNumber);
        MapperUtils.updateById(appDataTypeMapper, sourceData);
        return dataTypes;
    }
}
