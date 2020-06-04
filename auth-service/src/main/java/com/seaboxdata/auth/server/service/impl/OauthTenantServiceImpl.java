package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.dto.OauthRegisterTenantDTO;
import com.seaboxdata.auth.api.dto.OauthTenantParamDTO;
import com.seaboxdata.auth.api.dto.OauthTenantStatusDTO;
import com.seaboxdata.auth.api.enums.RoleEnum;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.api.vo.OauthResultTenantVO;
import com.seaboxdata.auth.api.vo.TenantInfoVO;
import com.seaboxdata.auth.server.dao.*;
import com.seaboxdata.auth.server.model.*;
import com.seaboxdata.auth.server.mq.AuthProducter;
import com.seaboxdata.auth.server.service.OauthTenantService;
import com.seaboxdata.auth.server.utils.AESUtils;
import com.seaboxdata.auth.server.utils.EncryptUtil;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 租户信息表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-21
 */
@Service
@Slf4j
public class OauthTenantServiceImpl implements OauthTenantService {

    @Autowired
    private OauthTenantMapper oauthTenantMapper;

    @Autowired
    private OauthTenantInfoMapper oauthTenantInfoMapper;

    @Autowired
    private OauthUserRoleMapper oauthUserRoleMapper;

    @Autowired
    private OauthUserMapper oauthUserMapper;

    @Autowired
    private OauthRoleMapper oauthRoleMapper;

    @Autowired
    private OauthPermissionMapper oauthPermissionMapper;

    @Autowired
    private OauthRolePermissionMapper oauthRolePermissionMapper;

    @Autowired
    private TenantCodeMapper tenantCodeMapper;

    @Autowired
    private PlatformCodeMapper platformCodeMapper;

    @Autowired
    private AuthProducter authProducter;

    private final String UCMANAGER = "ucManager";

    /**
     * @param tenantUserDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 添加 租户信息 + 用户信息
     * @date 2019/5/28
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveTenantUser(@RequestBody OauthRegisterTenantDTO tenantUserDTO) {
        if (Objects.isNull(tenantUserDTO)) {
            log.info("saveTenantUser -> param is null");
            throw new ServiceException("400", "添加租户信息参数为空");
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();

        LambdaQueryWrapper<OauthTenant> wrapper = new QueryWrapper<OauthTenant>().lambda()
                .eq(OauthTenant::getTenantName, tenantUserDTO.getTenantName().trim());

        // 若租户已存在
        if (Objects.nonNull(MapperUtils.getOne(oauthTenantMapper, wrapper, true))) {
            log.info("saveTenantUser -> 租户已存在");
            throw new ServiceException("700", "租户已存在");
        }

        OauthTenant oauthTenant;
        try {
            // 创建用户、设置关联角色 并 创建租户
            oauthTenant = handleTenantUser(tenantUserDTO);
        } catch (Exception e) {
            log.warn("error : {} ", e.getMessage());
            throw new ServiceException("500", "添加租户操作失败");
        }

        // 如果新建租户成功 则返回激活码
        if (Objects.nonNull(oauthTenant)) {
            // 租户创建成功 发送消息
            authProducter.saveTenantMsg(oauthTenant.getId(), oauthTenant.getTenantName()
                    , userDetails.getUserId(), oauthTenant.getId());
            // 生成激活码 设置tenantCodeId
            return getActivityCode(oauthTenant, tenantUserDTO);
        } else {
            throw new ServiceException("500", "添加租户操作失败,请重新尝试");
        }
    }

    /**
     * @param tenant, tenantUserDTO
     * @return java.lang.String
     * @author makaiyu
     * @description 生成激活码 设置tenantCodeId
     * @date 13:31 2019/9/4
     **/
    private String getActivityCode(OauthTenant tenant, OauthRegisterTenantDTO tenantUserDTO) {
        // 获取租户激活码
        String activityCode = AESUtils.generatorActivityCode(tenant.getId()
                , tenantUserDTO.getTenantLevelId());

        TenantCode getTenantCode = getTenantCodeByTenantId(tenant.getId());

        // 如果没有修改租户级别 则不生成激活码
        if (Objects.nonNull(getTenantCode) &&
                getTenantCode.getTenantLevelId().equals(tenantUserDTO.getTenantLevelId())) {
            return "already";
        }

        // 修改该租户TenantCode下状态
        LambdaUpdateWrapper<TenantCode> wrapper = new UpdateWrapper<TenantCode>().lambda()
                .eq(TenantCode::getTenantId, tenant.getId())
                .set(TenantCode::getStatus, false);

        MapperUtils.update(tenantCodeMapper, null, wrapper);

        // 创建tenantCode 状态设置为未激活
        TenantCode tenantCode = new TenantCode();
        tenantCode.setTenantLevelId(tenantUserDTO.getTenantLevelId())
                .setTenantId(tenant.getId())
                .setStatus(false);

        MapperUtils.save(tenantCodeMapper, tenantCode);
        tenant.setTenantCodeId(tenantCode.getId());
        MapperUtils.saveOrUpdate(oauthTenantMapper, tenant);
        return activityCode;
    }

    /**
     * @param tenantUserDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户ID及name 修改租户及管理员信息
     * @date 16:32 2019/5/28
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateTenantUser(@RequestBody OauthRegisterTenantDTO tenantUserDTO) {

        if (Objects.isNull(tenantUserDTO)) {
            log.info("updateTenantUser -> param is null");
            throw new ServiceException("400", "param is null");
        }

        // 1. 根据租户Id 获取原管理员Id
        LambdaQueryWrapper<OauthTenant> wrapper = new QueryWrapper<OauthTenant>().lambda()
                .eq(OauthTenant::getId, tenantUserDTO.getTenantId());

        OauthTenant tenant = MapperUtils.getOne(oauthTenantMapper, wrapper, true);

        log.info("updateTenantUser -> sourceTenant : {} ", tenant);

        if (Objects.isNull(tenant)) {
            throw new ServiceException("701", "修改租户信息-租户不存在");
        }

        Long userId = tenant.getUserId();

        // 2. 删除原管理员Id 对应的角色关联Id
        LambdaQueryWrapper<OauthUserRole> queryWrapper = new QueryWrapper<OauthUserRole>().lambda()
                .eq(OauthUserRole::getUserId, userId);

        MapperUtils.remove(oauthUserRoleMapper, queryWrapper);

        // 3. 创建用户、设置关联角色 并 创建/修改租户信息
        OauthTenant oauthTenant;
        try {
            oauthTenant = handleTenantUser(tenantUserDTO);
        } catch (Exception e) {
            log.warn("error:{}", e.getMessage());
            throw new ServiceException("500", "修改租户信息操作失败");
        }

        // 如果新建租户成功 则返回激活码
        if (Objects.nonNull(oauthTenant)) {
            // 生成激活码 设置tenantCodeId
            return getActivityCode(oauthTenant, tenantUserDTO);
        } else {
            return null;
        }
    }

    /**
     * @param oauthTenantParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.dto.OauthResultTenantDTO>
     * @author makaiyu
     * @description 获取全部租户信息
     * @date 10:28 2019/5/29
     */
    @Override
    public List<OauthResultTenantVO> selectAllTenant(@RequestBody(required = false)
                                                             OauthTenantParamDTO oauthTenantParamDTO) {

        LambdaQueryWrapper<OauthTenant> queryWrapper = new QueryWrapper<OauthTenant>().lambda();
        if (Objects.nonNull(oauthTenantParamDTO) && !StringUtils.isEmpty(oauthTenantParamDTO.getTenantId())) {
            queryWrapper.eq(OauthTenant::getId, oauthTenantParamDTO.getTenantId());
        }

        if (Objects.nonNull(oauthTenantParamDTO) && !StringUtils.isEmpty(oauthTenantParamDTO.getKeyWords())) {
            queryWrapper.like(OauthTenant::getTenantName, oauthTenantParamDTO.getKeyWords())
                    .or().like(OauthTenant::getTenantDesc, oauthTenantParamDTO.getKeyWords())
                    .or().like(OauthTenant::getTenantCode, oauthTenantParamDTO.getKeyWords());
        }

        List<OauthTenant> tenants = MapperUtils.list(oauthTenantMapper, queryWrapper);

        log.info("租户信息 tenants.size() :{}", tenants.size());

        List<OauthResultTenantVO> resultTenants = Lists.newArrayListWithCapacity(tenants.size());

        if (!CollectionUtils.isEmpty(tenants)) {
            for (OauthTenant tenant : tenants) {
                OauthResultTenantVO oauthResultTenant = new OauthResultTenantVO();
                BeanUtils.copyProperties(tenant, oauthResultTenant);
                LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                        .select(OauthUser::getName, OauthUser::getEmail,
                                OauthUser::getPhoneNumber, OauthUser::getPassword,
                                OauthUser::getUsername,OauthUser::getEnabled)
                        .eq(OauthUser::getId, tenant.getUserId());
                OauthUser user = MapperUtils.getOne(oauthUserMapper, wrapper, true);

                if (Objects.nonNull(user)) {
                    oauthResultTenant.setPassword(user.getPassword());
                    oauthResultTenant.setTenantId(tenant.getId());
                    oauthResultTenant.setUsername(user.getUsername());
                    oauthResultTenant.setManagerPhone(user.getPhoneNumber());
                    oauthResultTenant.setManagerName(user.getName());
                    oauthResultTenant.setManagerMail(user.getEmail());
                    oauthResultTenant.setEnabled(user.getEnabled());
                }
                oauthResultTenant.setTenantId(tenant.getId());

                // 获取租户级别
                TenantCode tenantCode = getTenantCodeByTenantId(tenant.getId());
                if (Objects.nonNull(tenantCode)) {
                    log.info("selectAllTenant -> 未获取到租户级别 tenantId:{}", tenant.getId());
                    oauthResultTenant.setTenantLevelId(tenantCode.getTenantLevelId());
                }

                // 获取租户详情表
                TenantInfoVO tenantInfoVO = getTenantInfo(tenant.getId());
                if (Objects.nonNull(tenantInfoVO)) {
                    log.info("selectAllTenant -> 未获取到租户详情 tenantId:{}", tenant.getId());
                    oauthResultTenant.setTenantInfoVO(tenantInfoVO);
                }

                // 获取租户激活码
                String activityCode = getActivityCodeByTenantId(tenant.getId());
                if (!StringUtils.isEmpty(activityCode)) {
                    oauthResultTenant.setActivityCode(activityCode);
                }

                resultTenants.add(oauthResultTenant);
            }
        }

        return resultTenants;
    }

    /**
     * @param tenantId
     * @return void
     * @author makaiyu
     * @description 根据租户Id 获取数据库中的激活码
     * @date 09:10 2020-02-07
     **/
    private String getActivityCodeByTenantId(Long tenantId) {
        LambdaQueryWrapper<TenantCode> wrapper = new QueryWrapper<TenantCode>().lambda()
                .eq(TenantCode::getTenantId, tenantId);

        List<TenantCode> codes = MapperUtils.list(tenantCodeMapper, wrapper);

        TenantCode tenantCode = null;

        if (!CollectionUtils.isEmpty(codes)) {
            Optional<TenantCode> tenantCodeOptional =
                    codes.stream().max(Comparator.comparing(TenantCode::getCreateTime));

            if (tenantCodeOptional.isPresent()) {
                tenantCode = tenantCodeOptional.get();
            }
        }

        if (Objects.nonNull(tenantCode)) {
            return tenantCode.getActivityCode();
        }

        return null;

    }

    /**
     * @param tenantId
     * @return com.seaboxdata.auth.api.vo.TenantInfoVO
     * @author makaiyu
     * @description 根据租户Id  获取租户详情表
     * @date 10:59 2019/11/15
     **/
    private TenantInfoVO getTenantInfo(Long tenantId) {

        LambdaQueryWrapper<OauthTenantInfo> wrapper = new QueryWrapper<OauthTenantInfo>().lambda()
                .eq(OauthTenantInfo::getTenantId, tenantId);

        OauthTenantInfo tenantInfo = MapperUtils.getOne(oauthTenantInfoMapper, wrapper, true);

        log.info("getTenantCodeByTenantId -> 获取codeList : {}", tenantInfo);

        TenantInfoVO tenantInfoVO = new TenantInfoVO();

        if (Objects.nonNull(tenantInfo)) {
            BeanUtils.copyProperties(tenantInfo, tenantInfoVO);
            tenantInfoVO.setTenantInfoId(tenantInfo.getId());
        }

        return tenantInfoVO;
    }

    /**
     * @param tenantId
     * @return java.lang.Long
     * @author makaiyu
     * @description 根据租户Id  获取租户级别
     * @date 14:19 2019/9/5
     **/
    private TenantCode getTenantCodeByTenantId(Long tenantId) {

        LambdaQueryWrapper<TenantCode> wrapper = new QueryWrapper<TenantCode>().lambda()
                .eq(TenantCode::getTenantId, tenantId)
                .select(TenantCode::getTenantLevelId)
                .orderByDesc(TenantCode::getCreateTime);

        List<TenantCode> codeList = MapperUtils.list(tenantCodeMapper, wrapper);

        log.info("getTenantCodeByTenantId -> 获取codeList : {}", codeList);

        if (CollectionUtils.isEmpty(codeList)) {
            return null;
        }
        return codeList.get(0);
    }

    /**
     * @param oauthTenantStatusDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据tenantIds 修改其启用状态
     * @date 11:02 2019/5/29
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTenantStatus(@RequestBody OauthTenantStatusDTO oauthTenantStatusDTO) {
        if (CollectionUtils.isEmpty(oauthTenantStatusDTO.getTenantId())) {
            log.info("updateTenantStatus -> param tenantIds is null");
            throw new ServiceException("400", "修改状态租户Id为空");
        }

        LambdaQueryWrapper<OauthTenant> wrapper = new QueryWrapper<OauthTenant>().lambda()
                .in(OauthTenant::getId, oauthTenantStatusDTO.getTenantId());

        List<OauthTenant> tenants = MapperUtils.list(oauthTenantMapper, wrapper);

        if (CollectionUtils.isEmpty(oauthTenantStatusDTO.getTenantId())) {
            log.info("租户不存在 tenantIds ;{}", oauthTenantStatusDTO.getTenantId());
            throw new ServiceException("701", "租户不存在");
        }

        try {
            tenants.forEach(tenant -> {
                // 如果状态为启用
                if (oauthTenantStatusDTO.getIsEnable()) {
                    LambdaUpdateWrapper<OauthTenant> zeroWrapper = new UpdateWrapper<OauthTenant>().lambda()
                            .eq(OauthTenant::getId, tenant.getId())
                            .set(OauthTenant::getStatus, NumberUtils.INTEGER_ZERO)
                            .set(OauthTenant::getUpdateTime, LocalDateTime.now());

                    MapperUtils.update(oauthTenantMapper, null, zeroWrapper);

                    handleUser(tenant, NumberUtils.LONG_ONE);
                } else {
                    // 将租户设置为禁用  同时将租户下的所有用户设置为禁用
                    LambdaUpdateWrapper<OauthTenant> oneWrapper = new UpdateWrapper<OauthTenant>().lambda()
                            .eq(OauthTenant::getId, tenant.getId())
                            .set(OauthTenant::getStatus, NumberUtils.INTEGER_ONE)
                            .set(OauthTenant::getUpdateTime, LocalDateTime.now());

                    MapperUtils.update(oauthTenantMapper, null, oneWrapper);

                    handleUser(tenant, NumberUtils.LONG_ZERO);
                }
            });
        } catch (Exception e) {
            log.warn("error : {} ", e.getMessage());
            throw new ServiceException("500", "修改启用状态失败");
        }

        return true;
    }


    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验租户可用数量
     * @date 13:46 2019/9/6
     **/
    @Override
    public Boolean checkTenantCount() {

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();

        Long userId = userDetails.getUserId();

        Integer tenantCount = MapperUtils.count(oauthTenantMapper, new QueryWrapper<>());

        LambdaQueryWrapper<PlatformCode> queryWrapper = new QueryWrapper<PlatformCode>().lambda()
                .eq(PlatformCode::getUserId, userId)
                .eq(PlatformCode::getStatus, true)
                .orderByDesc(PlatformCode::getCreateTime);

        List<PlatformCode> codeList = MapperUtils.list(platformCodeMapper, queryWrapper);

        if (CollectionUtils.isEmpty(codeList)) {
            log.info("平台激活码状态已变或数据被删除！");
            return false;
        }

        PlatformCode platformCode = codeList.get(0);

        if (platformCode.getTenantUseCount() > tenantCount) {
            return true;
        }

        return false;
    }

    /**
     * @param tenant, longOne
     * @return void
     * @author makaiyu
     * @description 处理禁用/启用租户 用户状态
     * @date 2019/8/29
     **/
    private void handleUser(OauthTenant tenant, Long longOne) {
        LambdaUpdateWrapper<OauthUser> userUpdateWrapper = new UpdateWrapper<OauthUser>().lambda()
                .eq(OauthUser::getTenantId, tenant.getId())
                .set(OauthUser::getEnabled, longOne)
                .set(OauthUser::getUpdateTime, LocalDateTime.now());

        MapperUtils.update(oauthUserMapper, null, userUpdateWrapper);
    }


    /**
     * @param tenantUserDTO
     * @return void
     * @author makaiyu
     * @description 处理创建用户、设置关联角色 并 创建租户
     * @date 16:54 2019/5/28
     **/
    private OauthTenant handleTenantUser(OauthRegisterTenantDTO tenantUserDTO) {

        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getUsername, tenantUserDTO.getUsername().trim());

        OauthUser user = MapperUtils.getOne(oauthUserMapper, wrapper, true);
        if (Objects.isNull(user)) {
            user = new OauthUser();
            user.setPassword(EncryptUtil.encryptPassword(tenantUserDTO.getPassword()));
        }
        // 1. 创建/修改用户
        user.setName(tenantUserDTO.getName())
                .setUsername(tenantUserDTO.getUsername())
                .setEmail(tenantUserDTO.getEmail())
                .setPhoneNumber(tenantUserDTO.getPhoneNumber())
                .setTenantId(NumberUtils.LONG_ONE)
                .setEnabled(tenantUserDTO.getEnabled())
                .setTenantManager(true);

        MapperUtils.saveOrUpdate(oauthUserMapper, user);

        // 2. 创建/修改租户信息
        OauthTenant oauthTenant = new OauthTenant();
        if (Objects.nonNull(tenantUserDTO.getTenantId())) {
            oauthTenant.setId(tenantUserDTO.getTenantId());
        }
        oauthTenant.setTenantDesc(tenantUserDTO.getTenantDesc())
                .setUserId(user.getId())
                .setTenantName(tenantUserDTO.getTenantName())
                .setTenantCode(tenantUserDTO.getTenantCode())
                .setUpdateTime(LocalDateTime.now());

        MapperUtils.saveOrUpdate(oauthTenantMapper, oauthTenant);

        log.info("创建租户信息 tenantId : {} ", oauthTenant.getId());

        // 3. 赋予超级管理员角色
        LambdaQueryWrapper<OauthRole> queryWrapper = new QueryWrapper<OauthRole>().lambda()
                .eq(OauthRole::getTenantId, oauthTenant.getId())
                .eq(OauthRole::getRoleCode, RoleEnum.SYSMANAGER.toString());

        OauthRole oauthRole = MapperUtils.getOne(oauthRoleMapper, queryWrapper, true);

        if (Objects.isNull(oauthRole)) {
            oauthRole = new OauthRole();
            oauthRole.setRoleCode(RoleEnum.SYSMANAGER.toString())
                    .setTenantId(oauthTenant.getId())
                    .setParentId(NumberUtils.LONG_ZERO)
                    .setRoleName("管理员")
                    .setStatus(NumberUtils.INTEGER_ONE)
                    .setDescription("管理员");
            MapperUtils.save(oauthRoleMapper, oauthRole);
            OauthUserRole oauthUserRole = new OauthUserRole();
            oauthUserRole.setUserId(user.getId())
                    .setRoleId(oauthRole.getId());

            MapperUtils.saveOrUpdate(oauthUserRoleMapper, oauthUserRole);

            // 角色关联UC权限
            LambdaQueryWrapper<OauthPermission> permissionWrapper = new QueryWrapper<OauthPermission>().lambda()
                    .eq(OauthPermission::getPermissionCode, UCMANAGER);

            OauthPermission permission = MapperUtils.getOne(oauthPermissionMapper, permissionWrapper, true);

            if (Objects.nonNull(permission)) {
                OauthRolePermission oauthRolePermission = new OauthRolePermission();
                oauthRolePermission.setRoleId(oauthRole.getId())
                        .setPermissionId(permission.getId())
                        .setAppName("UC");

                MapperUtils.saveOrUpdate(oauthRolePermissionMapper, oauthRolePermission);
            }
        } else {
            OauthUserRole oauthUserRole = new OauthUserRole();
            oauthUserRole.setUserId(user.getId())
                    .setRoleId(oauthRole.getId());

            MapperUtils.saveOrUpdate(oauthUserRoleMapper, oauthUserRole);
        }

        user.setTenantId(oauthTenant.getId());
        MapperUtils.saveOrUpdate(oauthUserMapper, user);
        return oauthTenant;
    }
}
