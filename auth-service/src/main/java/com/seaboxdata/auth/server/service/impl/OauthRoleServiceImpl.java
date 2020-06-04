package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seaboxdata.auth.api.dto.OauthRoleDTO;
import com.seaboxdata.auth.api.dto.OauthUserRoleDTO;
import com.seaboxdata.auth.api.enums.RoleEnum;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.api.vo.OauthRoleVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.dao.*;
import com.seaboxdata.auth.server.model.*;
import com.seaboxdata.auth.server.service.OauthRoleService;
import com.seaboxdata.auth.server.service.OauthUserService;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Service
@Slf4j
public class OauthRoleServiceImpl implements OauthRoleService {

    @Autowired
    private OauthRoleMapper oauthRoleMapper;

    @Autowired
    private OauthPermissionMapper oauthPermissionMapper;

    @Autowired
    private OauthRolePermissionMapper oauthRolePermissionMapper;

    @Autowired
    private OauthUserPermissionMapper oauthUserPermissionMapper;

    @Autowired
    private OauthUserService oauthUserService;

    @Autowired
    private OauthUserRoleMapper oauthUserRoleMapper;

    /**
     * @param userId
     * @return java.util.Set<com.seaboxdata.auth.model.SysRole>
     * @author makaiyu
     * @description 根据userId  获取 permissionCodes
     * @date 10:09 2019/5/14
     **/
    @Override
    public Set<String> selectPermissionCodeByUserId(Long userId) {

        if (Objects.isNull(userId)) {
            log.info("selectPermissionCodeByUserId -> param userId is null");
            return Sets.newConcurrentHashSet();
        }

        // 获取当前用户的所有权限码
        Set<OauthPermission> oauthPermissions = oauthPermissionMapper.selectPermissionCodeByUserId(userId);

        if (CollectionUtils.isEmpty(oauthPermissions)) {
            log.debug("selectPermissionCodeByUserId -> permissionCodes is null");
            return Sets.newConcurrentHashSet();
        }

        oauthPermissions.removeAll(Collections.singleton(null));

        Set<String> permissionCodes = oauthPermissions.stream().map(OauthPermission::getPermissionCode).collect(Collectors.toSet());

        LambdaQueryWrapper<OauthUserPermission> userPermissionWrapper = new QueryWrapper<OauthUserPermission>().lambda()
                .eq(OauthUserPermission::getUserId, userId);

        // 获取增量/减量 user -> permissionCode
        Set<OauthUserPermission> userPermissions = new HashSet<>(
                MapperUtils.list(oauthUserPermissionMapper, userPermissionWrapper));

        if (CollectionUtils.isEmpty(userPermissions)) {
            log.debug("selectPermissionCodeByUserId -> userPermissions is null");
            return permissionCodes;
        }

        userPermissions.forEach(userPermission -> {
            // 若status = 0  则表中存在增量资源权限
            if (NumberUtils.LONG_ZERO.equals(userPermission.getStatus())) {
                permissionCodes.add(userPermission.getPermissionCode());
            }
            if (NumberUtils.LONG_ONE.equals(userPermission.getStatus())) {
                permissionCodes.remove(userPermission.getPermissionCode());
            }
        });

        return permissionCodes;
    }


    /**
     * @return java.util.List<OauthRole>
     * @author makaiyu
     * @description 获取所有角色列表
     * @date 9:59 2019/5/20
     **/
    @Override
    public List<OauthRole> selectAllRole() {
        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();
        LambdaQueryWrapper<OauthRole> wrapper = new QueryWrapper<OauthRole>().lambda()
                .eq(OauthRole::getTenantId, tenantId);
        return MapperUtils.list(oauthRoleMapper, wrapper);
    }

    /**
     * @param userId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据用户Id获取所有角色Id
     * @date 10:46 2020-04-02
     **/
    @Override
    public List<Long> selectAllRoleByUserId(Long userId) {

        if (Objects.isNull(userId)) {
            return Lists.newArrayList();
        }

        LambdaQueryWrapper<OauthUserRole> wrapper = new QueryWrapper<OauthUserRole>().lambda()
                .eq(OauthUserRole::getUserId, userId);

        List<OauthUserRole> roleList = MapperUtils.list(oauthUserRoleMapper, wrapper);

        if (!CollectionUtils.isEmpty(roleList)) {
            return roleList.stream().map(OauthUserRole::getRoleId).collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }

    /**
     * @param oauthRoleDTO
     * @return Boolean
     * @author makaiyu
     * @description 添加角色 同时赋权限
     * @date 18:05 2019/5/27
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveUpdateOauthRole(@RequestBody OauthRoleDTO oauthRoleDTO) {
        if (Objects.isNull(oauthRoleDTO)) {
            log.info("saveUpdateOauthRole -> permissionIds and param is null");
            throw new ServiceException("400", "添加或保存角色和权限时,参数错误");
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        OauthRole oauthRole = new OauthRole();
        BeanUtils.copyProperties(oauthRoleDTO, oauthRole);

        oauthRole.setStatus(NumberUtils.INTEGER_ONE);
        oauthRole.setTenantId(tenantId);
        oauthRole.setUpdateTime(LocalDateTime.now());
        MapperUtils.saveOrUpdate(oauthRoleMapper, oauthRole);

        if (Objects.isNull(oauthRoleDTO.getPermissionsIds())) {
            return true;
        }
        // 获取角色Id
        Long roleId = oauthRole.getId();

        log.info("saveUpdateOauthRole -> roleId : {} ", roleId);

        // 获取设置角色对应权限Ids
        List<Long> permissionsIds = oauthRoleDTO.getPermissionsIds();

        log.info("saveUpdateOauthRole -> permissionsIds: {} ", permissionsIds);

        LambdaQueryWrapper<OauthRolePermission> wrapper = new QueryWrapper<OauthRolePermission>().lambda()
                .eq(OauthRolePermission::getRoleId, roleId);

        MapperUtils.remove(oauthRolePermissionMapper, wrapper);

        if (!CollectionUtils.isEmpty(permissionsIds)) {
            permissionsIds.forEach(permissionsId -> {
                OauthRolePermission oauthRolePermission = new OauthRolePermission();
                oauthRolePermission.setPermissionId(permissionsId);
                oauthRolePermission.setRoleId(roleId);
                MapperUtils.save(oauthRolePermissionMapper, oauthRolePermission);
            });
        }

        return true;
    }

    /**
     * @param roleIds
     * @return Boolean
     * @author makaiyu
     * @description 根据roleIds 删除角色 同时删除中间表
     * @date 18:19 2019/5/27
     **/
    @Override
    public Boolean deleteOauthRole(@RequestBody List<Long> roleIds) {

        if (CollectionUtils.isEmpty(roleIds)) {
            log.info("deleteOauthRole -> roleIds is null");
            throw new ServiceException("400", "roleIds is null");
        }

        roleIds.forEach(roleId -> {
            LambdaQueryWrapper<OauthRolePermission> wrapper = new QueryWrapper<OauthRolePermission>().lambda()
                    .eq(OauthRolePermission::getRoleId, roleId);
            MapperUtils.remove(oauthRolePermissionMapper, wrapper);
        });

        return MapperUtils.removeByIds(oauthRoleMapper, roleIds);
    }

    /**
     * @param userId, roleEnum
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断用户角色
     * @date 10:51 2019/8/19
     **/
    @Override
    public Boolean decideUserRole(Long userId, @RequestBody RoleEnum roleEnum) {

        List<OauthUserVO> oauthUserVOS = oauthUserService.selectUserByUserId(Lists.newArrayList(userId));
        if (CollectionUtils.isEmpty(oauthUserVOS)) {
            throw new ServiceException("701", "用户不存在");
        }

        OauthUserVO oauthUser = oauthUserVOS.get(0);

        List<OauthRoleVO> roles = oauthUser.getRoles();

        if (!CollectionUtils.isEmpty(roles)) {
            List<String> roleCode = roles.stream().map(OauthRoleVO::getRoleCode).collect(Collectors.toList());
            if (roleCode.contains(roleEnum.toString())) {
                return true;
            }

        }
        return false;
    }

    /**
     * @param userId, appName
     * @return java.util.Map<java.lang.Long, java.lang.String>
     * @author makaiyu
     * @description 根据用户Id、系统名称 查询其权限码及Id
     * @date 17:17 2019/11/25
     **/
    @Override
    public Map<Long, String> selectPermissionsByUserIdAndSystem(Long userId, String appName) {

        if (Objects.isNull(userId)) {
            log.info("selectPermissionsByUserIdAndSystem -> param userId is null");
            return Maps.newHashMap();
        }

        Set<OauthPermission> oauthPermissions;

        // 获取当前用户的所有权限码及权限码Id
        if (Objects.isNull(appName)) {
            oauthPermissions = oauthPermissionMapper.selectPermissionCodeByUserId(userId);
        } else {
            oauthPermissions = oauthPermissionMapper.selectPermissionsByUserIdAndSystem(userId, appName);
        }

        if (CollectionUtils.isEmpty(oauthPermissions)) {
            log.debug("selectPermissionsByUserIdAndSystem -> permissionCodes is null");
            return Maps.newHashMap();
        }

        return oauthPermissions.stream()
                .collect(Collectors.toMap(OauthPermission::getId, OauthPermission::getPermissionCode));
    }

    /**
     * @param oauthUserRoleDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个角色 -> 多个用户
     * @date 14:05 2020-04-26
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateUserRole(@RequestBody OauthUserRoleDTO oauthUserRoleDTO) {

        if (Objects.isNull(oauthUserRoleDTO) || Objects.isNull(oauthUserRoleDTO.getRoleId())) {
            log.info("saveOrUpdateUserRole -> param is null!");
            return false;
        }

        try {

            Long roleId = oauthUserRoleDTO.getRoleId();

            List<Long> userIds = Lists.newArrayList();
            if (!CollectionUtils.isEmpty(oauthUserRoleDTO.getAddUserId())) {
                userIds.addAll(oauthUserRoleDTO.getAddUserId());
            }

            if (!CollectionUtils.isEmpty(oauthUserRoleDTO.getDeleteUserId())) {
                userIds.addAll(oauthUserRoleDTO.getDeleteUserId());
            }

            if (CollectionUtils.isEmpty(userIds)) {
                log.info("saveOrUpdateUserGroup -> userIds is null!");
                return true;
            }

            // 1. 删除原有对应关系
            LambdaQueryWrapper<OauthUserRole> wrapper = new QueryWrapper<OauthUserRole>().lambda()
                    .in(OauthUserRole::getUserId, userIds)
                    .eq(OauthUserRole::getRoleId, roleId);

            MapperUtils.remove(oauthUserRoleMapper, wrapper);

            if (!CollectionUtils.isEmpty(oauthUserRoleDTO.getAddUserId())) {
                // 2. 增加新的对应关系
                oauthUserRoleDTO.getAddUserId().forEach(userId -> {
                    OauthUserRole oauthUserRole = new OauthUserRole();
                    oauthUserRole.setRoleId(roleId)
                            .setUserId(userId);
                    MapperUtils.save(oauthUserRoleMapper, oauthUserRole);
                });
            }
        } catch (Exception e) {
            log.error("saveOrUpdateUserRole -> ", e);
            throw new ServiceException("500", "修改角色匹配用户失败");
        }

        return true;
    }
}
