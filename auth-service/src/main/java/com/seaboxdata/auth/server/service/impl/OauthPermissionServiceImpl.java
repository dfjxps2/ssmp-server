package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seaboxdata.auth.api.dto.OauthPermissionDTO;
import com.seaboxdata.auth.api.dto.OauthRoleParamDTO;
import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.vo.OauthPermissionVO;
import com.seaboxdata.auth.server.dao.OauthPermissionMapper;
import com.seaboxdata.auth.server.dao.OauthRolePermissionMapper;
import com.seaboxdata.auth.server.dao.OauthUserMapper;
import com.seaboxdata.auth.server.model.OauthPermission;
import com.seaboxdata.auth.server.model.OauthRolePermission;
import com.seaboxdata.auth.server.service.OauthPermissionService;
import com.seaboxdata.commons.enums.AppKeyEnum;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 资源许可表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Service
@Slf4j
public class OauthPermissionServiceImpl implements OauthPermissionService {

    @Autowired
    private OauthRolePermissionMapper oauthRolePermissionMapper;

    @Autowired
    private OauthPermissionMapper oauthPermissionMapper;

    @Autowired
    private OauthUserMapper oauthUserMapper;

    /**
     * @param oauthSystemDTO
     * @return java.util.List<com.seaboxdata.auth.api.model.OauthPermission>
     * @author makaiyu
     * @description 获取permission列表
     * @date 17:55 2019/5/27
     */
    @Override
    public List<OauthPermissionVO> selectAllPermission(OauthSystemDTO oauthSystemDTO) {
        LambdaQueryWrapper<OauthPermission> wrapper = new QueryWrapper<OauthPermission>().lambda();

        if (Objects.nonNull(oauthSystemDTO)) {
            wrapper.eq(OauthPermission::getAppName, oauthSystemDTO.getAppName());
        }

        List<OauthPermission> oauthPermissions = MapperUtils.list(oauthPermissionMapper, wrapper);

        List<OauthPermissionVO> oauthPermissionVOS = Lists.newArrayListWithCapacity(oauthPermissions.size());

        if (!CollectionUtils.isEmpty(oauthPermissions)) {
            oauthPermissions.forEach(oauthPermission -> {
                OauthPermissionVO oauthPermissionVO = new OauthPermissionVO();
                BeanUtils.copyProperties(oauthPermission, oauthPermissionVO);

                oauthPermissionVO.setPermissionId(oauthPermission.getId());
                oauthPermissionVOS.add(oauthPermissionVO);
            });
        }

        return oauthPermissionVOS;
    }

    /**
     * @param oauthRoleParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.dto.OauthPermissionDTO>
     * @author makaiyu
     * @description 根据roleId 获取对应permission
     * @date 17:22 2019/6/3
     **/
    @Override
    public List<OauthPermissionVO> selectPermissionByRoleId(@RequestBody OauthRoleParamDTO oauthRoleParamDTO) {

        if (Objects.isNull(oauthRoleParamDTO)) {
            log.info("selectPermissionByRoleId -> param is null");
            throw new ServiceException("400", "角色Id为空");
        }

        // 1. 获取所有权限信息
        LambdaQueryWrapper<OauthPermission> permissionWrapper = new QueryWrapper<OauthPermission>().lambda();

        if (Objects.nonNull(oauthRoleParamDTO.getAppName())) {
            permissionWrapper.eq(OauthPermission::getAppName, oauthRoleParamDTO.getAppName());
        }

        List<OauthPermission> permissions = MapperUtils.list(oauthPermissionMapper, permissionWrapper);

        // 2. 获取该角色对应的权限信息 将其标注为true
        LambdaQueryWrapper<OauthRolePermission> wrapper = new QueryWrapper<OauthRolePermission>().lambda()
                .eq(OauthRolePermission::getRoleId, oauthRoleParamDTO.getRoleId());

        if (Objects.nonNull(oauthRoleParamDTO.getAppName())) {
            wrapper.eq(OauthRolePermission::getAppName, oauthRoleParamDTO.getAppName());
        }

        List<Long> permissionIds = MapperUtils.list(oauthRolePermissionMapper, wrapper)
                .stream().map(OauthRolePermission::getPermissionId).collect(Collectors.toList());

        List<OauthPermissionVO> oauthPermissionVOS = Lists.newArrayListWithCapacity(permissions.size());


        if (!CollectionUtils.isEmpty(permissions)) {
            permissions.forEach(permission -> {
                OauthPermissionVO oauthPermissionVO = new OauthPermissionVO();
                BeanUtils.copyProperties(permission, oauthPermissionVO);

                oauthPermissionVO.setPermissionId(permission.getId());

                if (permissionIds.contains(permission.getId())) {
                    oauthPermissionVO.setFlag(true);
                } else {
                    oauthPermissionVO.setFlag(false);
                }
                oauthPermissionVOS.add(oauthPermissionVO);
            });
        }
        return oauthPermissionVOS;
    }

    /**
     * @param permissionIds
     * @param roleId
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据角色添加权限
     * @date 17:35 2019/6/3
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdatePermissionByRoleId(@RequestBody List<Long> permissionIds, Long roleId,
                                                  String appName) {

        if (Objects.isNull(roleId)) {
            log.info("saveOrUpdatePermissionByRoleId -> permissionIds || roleId is null");
            throw new ServiceException("400", "参数错误！角色Id为空");
        }

        try {
            LambdaQueryWrapper<OauthRolePermission> wrapper = new QueryWrapper<OauthRolePermission>().lambda()
                    .eq(OauthRolePermission::getRoleId, roleId)
                    .eq(OauthRolePermission::getAppName, appName);

            MapperUtils.remove(oauthRolePermissionMapper, wrapper);

            if (CollectionUtils.isEmpty(permissionIds)) {
                return true;
            }

            permissionIds.forEach(permissionId -> {
                OauthRolePermission oauthRolePermission = new OauthRolePermission();
                oauthRolePermission.setPermissionId(permissionId)
                        .setRoleId(roleId)
                        .setAppName(appName);
                MapperUtils.save(oauthRolePermissionMapper, oauthRolePermission);
            });
        } catch (Exception e) {
            log.warn("添加角色权限信息错误 : {}", e.getMessage());
            throw new ServiceException("500", "角色权限添加失败");
        }

        return true;
    }

    /**
     * @param oauthPermissionDTOS
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 批量增加权限许可
     * @date 18:50 2019/6/6
     **/
    @Override
    public Boolean savePermissions(@RequestBody List<OauthPermissionDTO> oauthPermissionDTOS) {

        if (CollectionUtils.isEmpty(oauthPermissionDTOS)) {
            log.info("savePermissions param is null");
            return false;
        }

        try {
            oauthPermissionDTOS.forEach(oauthPermissionDTO -> {
                OauthPermission oauthPermission = new OauthPermission();
                BeanUtils.copyProperties(oauthPermissionDTO, oauthPermission);
                MapperUtils.save(oauthPermissionMapper, oauthPermission);
            });
        } catch (Exception e) {
            throw new ServiceException("500", "添加失败");
        }

        return true;
    }

    /**
     * @param permissionCodes
     * @param tenantId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据权限码获取拥有权限的人
     * @date 15:39 2019/10/28
     **/
    @Override
    public List<Long> selectUserIdByPermissionCodes(@RequestBody List<String> permissionCodes, Long tenantId) {

        if (CollectionUtils.isEmpty(permissionCodes)) {
            return Lists.newArrayList();
        }

        if (Objects.isNull(tenantId)) {
            log.info("selectUserIdByPermissionCodes --> tenantId: {}", tenantId);
            tenantId = NumberUtils.LONG_ZERO;
        }

        return oauthPermissionMapper.selectUserIdByPermissionCodes(permissionCodes, tenantId);
    }

    /**
     * @param permissionIds
     * @return java.util.Map<java.lang.Long, java.lang.Long>
     * @author makaiyu
     * @description 根据权限码Id 查找其父Id
     * @date 17:20 2019/11/25
     **/
    @Override
    public Map<Long, Long> selectPermissionParentByIds(@RequestBody List<Long> permissionIds) {

        if (CollectionUtils.isEmpty(permissionIds)) {
            log.info("selectPermissionParentByIds -> permissionIds is null");
            return Maps.newHashMap();
        }

        LambdaQueryWrapper<OauthPermission> wrapper = new QueryWrapper<OauthPermission>().lambda()
                .in(OauthPermission::getId, permissionIds);

        List<OauthPermission> permissions = MapperUtils.list(oauthPermissionMapper, wrapper);

        if (CollectionUtils.isEmpty(permissionIds)) {
            log.info("selectPermissionParentByIds -> permissions is null");
            return Maps.newHashMap();
        }

        return permissions.stream().collect(Collectors.toMap(OauthPermission::getId, OauthPermission::getParentId));
    }

    /**
     * @param permissionId, parentId,appName
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改permissionId所处父Id
     * @date 17:23 2019/11/25
     **/
    @Override
    public Boolean updatePermissionParentById(Long permissionId, Long parentId, String appName) {
        AppKeyEnum appKey;
        try {
            appKey = AppKeyEnum.valueOf(appName);
        } catch (Exception e) {
            log.error("updatePermissionParentById -> AppName cannot be converted to appKeyEnum");
            return false;
        }

        LambdaUpdateWrapper<OauthPermission> wrapper = new UpdateWrapper<OauthPermission>().lambda()
                .eq(OauthPermission::getAppName, appKey)
                .eq(OauthPermission::getId, permissionId)
                .set(OauthPermission::getParentId, parentId);

        return MapperUtils.update(oauthPermissionMapper, null, wrapper);
    }

    /**
     * @param permissionCodes
     * @return String
     * @author makaiyu
     * @description 根据权限码 获取对应系统
     * @date 11:03 2020-03-23
     **/
    @Override
    public Set<String> selectAppKeyByPermissionCodes(Set<String> permissionCodes) {
        if (CollectionUtils.isEmpty(permissionCodes)) {
            return Sets.newHashSet();
        }

        LambdaQueryWrapper<OauthPermission> wrapper = new QueryWrapper<OauthPermission>().lambda()
                .in(OauthPermission::getPermissionCode, permissionCodes)
                .select(OauthPermission::getAppName);

        List<OauthPermission> permissions = MapperUtils.list(oauthPermissionMapper, wrapper);

        Set<String> result = Sets.newHashSetWithExpectedSize(permissions.size());

        if (!CollectionUtils.isEmpty(permissions)) {
            Set<AppKeyEnum> appKeyEnums = permissions.stream().map(OauthPermission::getAppName).collect(Collectors.toSet());

            appKeyEnums.forEach(appKeyEnum -> result.add(appKeyEnum.toString()));
            return result;
        }

        return Sets.newHashSet();
    }
}
