package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seaboxdata.auth.api.dto.*;
import com.seaboxdata.auth.api.dto.domain.Token;
import com.seaboxdata.auth.api.utils.BeanUtils;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.*;
import com.seaboxdata.auth.server.dao.*;
import com.seaboxdata.auth.server.mapstruct.GraduationConverter;
import com.seaboxdata.auth.server.mapstruct.SkillInfoConverter;
import com.seaboxdata.auth.server.mapstruct.StaffLevelConverter;
import com.seaboxdata.auth.server.model.*;
import com.seaboxdata.auth.server.mq.AuthProducter;
import com.seaboxdata.auth.server.service.*;
import com.seaboxdata.auth.server.utils.DataHelper;
import com.seaboxdata.auth.server.utils.EncryptUtil;
import com.seaboxdata.auth.server.utils.RedisUtil;
import com.seaboxdata.auth.server.utils.RefreshTokenUtils;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import com.seaboxdata.commons.query.PaginationResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Service
@Slf4j
public class OauthUserServiceImpl implements OauthUserService {

    @Autowired
    private GraduationConverter graduationConverter;

    @Autowired
    private StaffLevelConverter staffLevelConverter;

    @Autowired
    private StaffLevelService staffLevelService;

    @Autowired
    private SkillInfoConverter skillInfoConverter;

    @Autowired
    private SkillInfoMapper skillInfoMapper;

    @Autowired
    private GraduationInfoMapper graduationInfoMapper;

    @Autowired
    private GraduationInfoService graduationInfoService;

    @Autowired
    private SkillInfoService skillInfoService;


    @Autowired
    private OauthUserRoleMapper oauthUserRoleMapper;

    @Autowired
    private OauthUserPermissionMapper oauthUserPermissionMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RefreshTokenUtils refreshTokenUtils;

    @Autowired
    private RedisTokenStore redisTokenStore;

    @Autowired
    private OauthUserInfoMapper oauthUserInfoMapper;

    @Autowired
    private OauthUserGroupMapper oauthUserGroupMapper;

    @Autowired
    private OauthUserMapper oauthUserMapper;

    @Autowired
    private OauthRoleMapper oauthRoleMapper;

    @Autowired
    private OauthGroupMapper oauthGroupMapper;

    @Autowired
    private OauthUserOrganizationMapper oauthUserOrganizationMapper;

    @Autowired
    private OauthOrganizationMapper oauthOrganizationMapper;

    @Autowired
    private OauthRoleService oauthRoleService;

    @Autowired
    private TenantCodeMapper tenantCodeMapper;

    @Autowired
    private OauthTenantInfoService tenantInfoService;

    @Autowired
    private TenantLevelService tenantLevelService;

    @Autowired
    private OauthTenantMapper oauthTenantMapper;

    @Autowired
    private AuthProducter authProducter;

    @Autowired
    private OauthUserService oauthUserService;

    @Autowired
    private OauthAddressLogMapper oauthAddressLogMapper;

    @Autowired
    private OauthPermissionService oauthPermissionService;

    /**
     * 鉴权模式
     */
    public static final String[] GRANT_TYPE = {"password", "refresh_token"};

    @Override
    public OauthUser getByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            throw new ServiceException("400", "根据username获取用户信息失败");
        }
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getUsername, username.trim());
        return MapperUtils.getOne(oauthUserMapper, wrapper, true);
    }

    /**
     * @param user
     * @return Boolean
     * @author makaiyu
     * @description 用户注册
     * @date 2019/5/13
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean registerUser(@RequestBody OauthSaveUserDTO user) {

        if (Objects.isNull(user)) {
            log.info("registerUser -> user is null");
            throw new ServiceException("400", "param is null");
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();
        Long loginUserId = userDetails.getUserId();
        /* 默认用户状态设置为开启 */
        if(Objects.isNull(user.getEnabled()))
        {
            user.setEnabled(true);
        }

        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getUsername, user.getUsername());

        OauthUser oauthUser = MapperUtils.getOne(oauthUserMapper, wrapper, true);

        if (Objects.nonNull(oauthUser)) {
            log.info("registerUser -> username is already");
            throw new ServiceException("702", "username is already");
        }

        try {
            // 校验该租户级别 是否允许创建新用户
            Boolean flag = tenantLevelService.checkTenantLevel();

            if (!flag) {
                throw new ServiceException("1400", "当前租户可创建用户数已达上限，请升级租户等级");
            }

            // 处理新增用户
            Long userId = handleUserInfo(user, tenantId);
            try {
                authProducter.saveUserTag(userId, "", loginUserId, tenantId);
            } catch (Exception e) {
                log.error("用户创建 mq消息发送失败 ！");
            }
        } catch (Exception e) {
            log.error("用户创建异常", e);
            throw new ServiceException("500", "用户新建失败");
        }

        return true;
    }

    /**
     * @param userIds
     * @return Boolean
     * @author makaiyu
     * @description 根据UserId  删除User
     * @date 9:31 2019/5/20
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserById(@RequestBody List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            log.info("deleteUserById -> param is null");
            throw new ServiceException("400", "删除用户参数为空");
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long userId = userDetails.getUserId();
        Long tenantId = userDetails.getTenantId();

        try {
            LambdaQueryWrapper<OauthUserRole> roleWrapper = new QueryWrapper<OauthUserRole>().lambda()
                    .in(OauthUserRole::getUserId, userIds);
            // 删除user_role关联信息
            MapperUtils.remove(oauthUserRoleMapper, roleWrapper);

            LambdaQueryWrapper<OauthUserPermission> permissionWrapper = new QueryWrapper<OauthUserPermission>().lambda()
                    .in(OauthUserPermission::getUserId, userIds)
                    .eq(OauthUserPermission::getTenantId, tenantId);

            // 删除赋予权限信息
            MapperUtils.remove(oauthUserPermissionMapper, permissionWrapper);

            LambdaQueryWrapper<OauthUserInfo> userInfoWrapper = new QueryWrapper<OauthUserInfo>().lambda()
                    .in(OauthUserInfo::getUserId, userIds);

            // 删除用户联系方式
            MapperUtils.remove(oauthUserInfoMapper, userInfoWrapper);

            LambdaQueryWrapper<OauthUserOrganization> deptWrapper = new QueryWrapper<OauthUserOrganization>().lambda()
                    .in(OauthUserOrganization::getUserId, userIds);

            // 删除用户部门信息
            MapperUtils.remove(oauthUserOrganizationMapper, deptWrapper);

            // 删除用户信息
            LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                    .in(OauthUser::getId, userIds)
                    .eq(OauthUser::getSystemUser, false)
                    .eq(OauthUser::getTenantId, UserUtils.getUserDetails().getTenantId());

            MapperUtils.remove(oauthUserMapper, wrapper);

            // 删除用户毕业相关信息
            MapperUtils.remove(graduationInfoMapper, Wrappers.lambdaQuery(new GraduationInfo())
                    .in(GraduationInfo::getUserId, userIds));

            // 删除用户技能信息
            MapperUtils.remove(skillInfoMapper, Wrappers.lambdaQuery(new SkillInfo())
                    .in(SkillInfo::getUserId, userIds));

            try {
                // 删除用户 发送消息
                userIds.forEach(authUserId -> {
                    authProducter.delUserTag(authUserId, "", userId, tenantId);
                });
            } catch (Exception e) {
                log.error("用户删除 mq消息发送失败 ！");
            }
        } catch (Exception e) {
            throw new ServiceException("500", "删除用户失败");
        }

        return true;
    }

    /**
     * @return java.util.List<OauthUser>
     * @author makaiyu
     * @description 获取全部用户
     * @date 9:56 2019/5/20
     **/
    @Override
    public PaginationResult<OauthUserVO> selectAllUser(@RequestBody(required = false) OauthUserNamePageDTO pageDTO) {

        if (Objects.isNull(pageDTO)) {
            return new PaginationResult<>();
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();
        List<Long> groupUserIds = Lists.newArrayList();
        List<Long> organizationUserIds = Lists.newArrayList();

        if (Objects.nonNull(pageDTO.getGroupId())) {
            // 判断分组Id是否为空
            groupUserIds = getGroupsForUser(pageDTO);
        }

        if (Objects.nonNull(pageDTO.getOrganizationId())) {
            organizationUserIds = getOrganizationUserIds(pageDTO, tenantId);
        }

        // 1. 根据租户ID 获取全部用户
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getTenantId, tenantId);

        // 更具员工等级过滤，如果存在员工等级id
        if (Objects.nonNull(pageDTO.getStaffLevelId())) {
            wrapper.eq(OauthUser::getStaffLevelId, pageDTO.getStaffLevelId());
        }

        List<Long> allUserIds = Lists.newArrayList();
        allUserIds.addAll(groupUserIds);
        allUserIds.addAll(organizationUserIds);

        if (allUserIds.size() == NumberUtils.INTEGER_ZERO
                && (Objects.nonNull(pageDTO.getGroupId())
                || Objects.nonNull(pageDTO.getOrganizationId()))) {
            return new PaginationResult<>();
        }

        convUserWrapper(pageDTO, wrapper, allUserIds);

        List<OauthUser> oauthUsers = Lists.newArrayList();

        // key: roleId   value: OauthRole
        Map<Long, OauthRole> roleMap = MapperUtils.list(oauthRoleMapper, new QueryWrapper<>())
                .stream().collect(Collectors.toMap(OauthRole::getId, Function.identity()));

        List<OauthUserVO> oauthUserVOS = Lists.newArrayList();

        // 员工等级设置
        Map<Long, StaffLevel> levelMap = Maps.newHashMap();

        getUserLevel(oauthUsers, levelMap);

        // 员工毕业信息设置
        Map<Long, List<GraduationInfo>> graduationMap = Maps.newHashMap();

        getUserGraduation(oauthUsers, graduationMap);

        // 员工技能信息设置
        Map<Long, List<SkillInfo>> skillMap = Maps.newHashMap();

        getUserSkill(oauthUsers, skillMap);

        // 直属领导设置
        Map<Long, OauthUser> managerUserMap = Maps.newHashMapWithExpectedSize(oauthUsers.size());

        getUserLeader(oauthUsers, managerUserMap);

        Page<OauthUser> userPage = null;
        // 如果为分页  则获取全部
        if (Objects.isNull(pageDTO.getOffset()) && Objects.isNull(pageDTO.getLimit())) {
            oauthUsers = MapperUtils.list(oauthUserMapper, wrapper);
            List<OauthUserVO> finalOauthUserVOS = oauthUserVOS;
            oauthUsers.forEach(user -> {
                OauthUserVO oauthUserVO = convUserInfo(roleMap, levelMap, graduationMap, skillMap, managerUserMap, user);
                finalOauthUserVOS.add(oauthUserVO);
            });
            oauthUserVOS = finalOauthUserVOS;
        } else {
            userPage = (Page<OauthUser>) MapperUtils.page(oauthUserMapper,
                    new Page<>(pageDTO.getOffset(), pageDTO.getLimit()), wrapper);

            List<OauthUserVO> finalOauthUserVOS = oauthUserVOS;

            userPage.getRecords().forEach(user -> {
                OauthUserVO oauthUserVO = convUserInfo(roleMap, levelMap, graduationMap, skillMap, managerUserMap, user);

                finalOauthUserVOS.add(oauthUserVO);
            });
            oauthUserVOS = finalOauthUserVOS;
        }

        // 2. 封装Page对象
        return convResult(pageDTO, oauthUsers, oauthUserVOS, userPage);
    }

    /**
     * @param pageDTO, oauthUsers, oauthUserVOS, userPage
     * @return com.seaboxdata.commons.query.PaginationResult<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 封装返回对象
     * @date 17:43 2020-05-11
     **/
    private PaginationResult<OauthUserVO> convResult(@RequestBody(required = false) OauthUserNamePageDTO pageDTO, List<OauthUser> oauthUsers, List<OauthUserVO> oauthUserVOS, Page<OauthUser> userPage) {
        PaginationResult<OauthUserVO> paginationResult = new PaginationResult<>();
        paginationResult.setData(CollectionUtils.isEmpty(oauthUserVOS) ? Lists.newArrayList() : oauthUserVOS);
        if (Objects.nonNull(userPage)) {
            paginationResult.setTotal((int) userPage.getTotal());
            paginationResult.setLimit((int) userPage.getSize());
        } else {
            paginationResult.setTotal(oauthUsers.size());
        }
        paginationResult.setOffset(pageDTO.getOffset());
        return paginationResult;
    }

    /**
     * @param roleMap, levelMap, graduationMap, skillMap, managerUserMap, user
     * @return com.seaboxdata.auth.api.vo.OauthUserVO
     * @author makaiyu
     * @description 拼接用户技能信息等
     * @date 17:42 2020-05-11
     **/
    private OauthUserVO convUserInfo(Map<Long, OauthRole> roleMap, Map<Long, StaffLevel> levelMap, Map<Long,
            List<GraduationInfo>> graduationMap, Map<Long, List<SkillInfo>> skillMap,
                                     Map<Long, OauthUser> managerUserMap, OauthUser user) {
        OauthUserVO oauthUserVO = setOauthUserVO(roleMap, user);
        managerUserMap.forEach((userId, oauthUser) -> {
            if (user.getId().equals(userId)) {
                OauthUserVO vo = new OauthUserVO();
                org.springframework.beans.BeanUtils.copyProperties(oauthUser, vo);
                oauthUserVO.setDirectLeader(vo);
            }
        });

        levelMap.forEach((userId, staffLevel) -> {
            if (user.getId().equals(userId)) {
                oauthUserVO.setStaffLevelVo(staffLevelConverter.toStaffLevelVo(staffLevel));
            }
        });

        graduationMap.forEach((userId, graduationInfos) -> {
            if (user.getId().equals(userId)) {
                oauthUserVO.setGraduationInfoVos(graduationConverter.toGraduationInfoVos(graduationInfos));
            }
        });

        skillMap.forEach((userId, skillInfos) -> {
            if (user.getId().equals(userId)) {
                oauthUserVO.setSkillInfoVos(skillInfoConverter.toSkillInfoVos(skillInfos));
            }
        });
        return oauthUserVO;
    }

    /**
     * @return java.util.List<java.lang.String>
     * @author makaiyu
     * @description 查询全部username
     * @date 15:57 2020-01-06
     **/
    @Override
    public List<String> selectAllUserName() {

        LambdaQueryWrapper<OauthUser> select = new QueryWrapper<OauthUser>().lambda()
                .select(OauthUser::getUsername);

        List<OauthUser> users = MapperUtils.list(oauthUserMapper, select);

        if (CollectionUtils.isEmpty(users)) {
            return Lists.newArrayList();
        }

        return users.stream().map(OauthUser::getUsername).collect(toList());
    }

    /**
     * @param roleMap, user
     * @return com.seaboxdata.auth.api.vo.OauthUserVO
     * @author makaiyu
     * @description 组装用户信息
     * @date 11:23 2019/9/23
     **/
    private OauthUserVO setOauthUserVO(Map<Long, OauthRole> roleMap, OauthUser user) {
        OauthUserVO oauthUserVO = new OauthUserVO();
        List<OauthRoleVO> oauthRoles = Lists.newArrayListWithCapacity(roleMap.size());
        List<OauthUserInfoVO> oauthUserInfo = Lists.newArrayListWithCapacity(roleMap.size());
        List<OauthGroupVO> oauthGroupVOS = Lists.newArrayListWithCapacity(roleMap.size());
        List<OauthOrganizationVO> oauthOrganizationVOS = Lists.newArrayListWithCapacity(roleMap.size());
        BeanUtils.copyPropertiesIgnoreNull(user, oauthUserVO);
        oauthUserVO.setUserId(user.getId());

        // 获取该用户下 所有角色Id
        selectUserDetail(user, oauthUserVO, roleMap, oauthRoles, oauthGroupVOS,
                oauthOrganizationVOS, oauthUserInfo);
        // 设置 用户 毕业、技能、等级相关信息
//        setUserGraduationAndSkillInfos(oauthUserVO, user.getStaffLevelId());

        return oauthUserVO;
    }

    /**
     * @param user
     * @return com.seaboxdata.auth.api.vo.OauthUserVO
     * @author makaiyu
     * @description 组装用户信息
     * @date 11:23 2019/9/23
     **/
    private OauthJxpmUserVO setOauthJxpmUserVO(OauthUser user) {
        OauthJxpmUserVO oauthUserVO = new OauthJxpmUserVO();

        org.springframework.beans.BeanUtils.copyProperties(user, oauthUserVO);

        // 查询当前用户直属领导
        List<OauthUserVO> oauthUserVOS = selectUserByUserId(Collections.singletonList(user.getDirectLeader()));
        if (!CollectionUtils.isEmpty(oauthUserVOS)) {
            oauthUserVO.setDirectLeader(oauthUserVOS.get(0));
        }

        oauthUserVO.setStaffLevelVo(staffLevelConverter.toStaffLevelVo(
                staffLevelService.getOne(
                        Wrappers.lambdaQuery(new StaffLevel()).eq(StaffLevel::getId, user.getStaffLevelId())
                )
        ));

        return oauthUserVO;
    }


    /**
     * 设置 用户 毕业、技能、等级相关信息
     *
     * @param oauthUserVO,staffLevelId
     */
    private void setUserGraduationAndSkillInfos(OauthUserVO oauthUserVO, Long staffLevelId) {
        oauthUserVO.setGraduationInfoVos(   //用户毕业相关信息
                graduationConverter.toGraduationInfoVos(
                        graduationInfoService.list(
                                Wrappers.lambdaQuery(new GraduationInfo()).eq(GraduationInfo::getUserId, oauthUserVO.getUserId())
                        )
                )
        ).setSkillInfoVos(      // 技能信息
                skillInfoConverter.toSkillInfoVos(
                        skillInfoService.list(
                                Wrappers.lambdaQuery(new SkillInfo()).eq(SkillInfo::getUserId, oauthUserVO.getUserId())
                        )
                )
        ).setStaffLevelVo(      // 员工等级
                staffLevelConverter.toStaffLevelVo(
                        staffLevelService.getOne(
                                Wrappers.lambdaQuery(new StaffLevel()).eq(StaffLevel::getId, staffLevelId)
                        )
                )
        );
    }

    /**
     * @param pageDTO
     * @param tenantId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 获取机构及子机构下的用户Id
     * @date 10:12 2019/8/26
     **/
    private List<Long> getOrganizationUserIds(@RequestBody(required = false) OauthUserNamePageDTO pageDTO, Long tenantId) {
        // 判断机构Id是否为空
        List<Long> organizationUserIds;
        Long organizationId = pageDTO.getOrganizationId();

        Set<Long> allOrganizationIds = Sets.newHashSet(organizationId);

        // 获取其下子机构的信息
        Set<Long> result = Sets.newHashSet();
        setByOrganizationId(Sets.newHashSet(organizationId), result, tenantId);

        if (!CollectionUtils.isEmpty(result)) {
            LambdaQueryWrapper<OauthOrganization> wrapper = new QueryWrapper<OauthOrganization>().lambda()
                    .in(OauthOrganization::getId, result);
            List<OauthOrganization> list = MapperUtils.list(oauthOrganizationMapper, wrapper);
            if (!CollectionUtils.isEmpty(list)) {
                allOrganizationIds.addAll(list.stream().map(OauthOrganization::getId).collect(toList()));
            }
        }

        allOrganizationIds.add(organizationId);

        LambdaQueryWrapper<OauthUserOrganization> organizationWrapper = new QueryWrapper<OauthUserOrganization>().lambda();
        organizationWrapper.in(OauthUserOrganization::getOrganizationId, allOrganizationIds);
        organizationUserIds = MapperUtils.list(oauthUserOrganizationMapper, organizationWrapper)
                .stream().map(OauthUserOrganization::getUserId).collect(toList());
        return organizationUserIds;
    }

    /**
     * @param organizationIds
     * @param results
     * @param tenantId
     * @return java.util.Set<java.lang.Long>
     * @author makaiyu
     * @description 递归获取其下自机构
     * @date 10:59 2020-04-28
     **/
    private Set<Long> setByOrganizationId(Set<Long> organizationIds, Set<Long> results, Long tenantId) {

        if (!CollectionUtils.isEmpty(organizationIds)) {
            LambdaQueryWrapper<OauthOrganization> wrapper = new QueryWrapper<OauthOrganization>().lambda()
                    .in(OauthOrganization::getParentId, organizationIds)
                    .eq(OauthOrganization::getTenantId, tenantId);
            List<OauthOrganization> organizations = MapperUtils.list(oauthOrganizationMapper, wrapper);
            if (!CollectionUtils.isEmpty(organizations)) {
                Set<Long> ids = organizations.stream().map(OauthOrganization::getId).collect(Collectors.toSet());
                results.addAll(ids);
                setByOrganizationId(ids, results, tenantId);
            }
        }
        return results;
    }

    /**
     * @param loginUserDTO
     * @return com.seaboxdata.commons.utils.Resp
     * @author makaiyu
     * @description 登陆
     * @date 10:35 2019/5/20
     **/
    @Override
    public OauthUserVO login(@RequestBody LoginUserDTO loginUserDTO) {

        if (Objects.isNull(loginUserDTO.getUsername()) || Objects.isNull(loginUserDTO.getPassword())) {
            throw new ServiceException("605", "用户名或密码为空");
        }

        if (Objects.isNull(loginUserDTO.getClientId()) || Objects.isNull(loginUserDTO.getClientSecret())) {
            throw new ServiceException("606", "鉴权client信息为空");
        }

        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", loginUserDTO.getClientId());
        paramMap.add("client_secret", loginUserDTO.getClientSecret());
        paramMap.add("username", loginUserDTO.getUsername());
        paramMap.add("password", loginUserDTO.getPassword());
        paramMap.add("grant_type", GRANT_TYPE[0]);
        Token token = new Token();
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getUsername, loginUserDTO.getUsername().trim());
        OauthUser userPO = MapperUtils.getOne(oauthUserMapper, wrapper, true);
        if (Objects.isNull(userPO)) {
            throw new ServiceException("603", "用户不存在");
        }

        if (!userPO.getEnabled()) {
            throw new ServiceException("606", "当前用户已禁止登录，请联系管理员!");
        }

        try {
            //因为oauth2本身自带的登录接口是"/oauth/token"，并且返回的数据类型不能按我们想要的去返回
            //所以这里用restTemplate(HTTP客户端)进行一次转发到oauth2内部的登录接口
            tokenHandler(paramMap, token);
        } catch (Exception e) {
            if (e.getMessage().contains(String.valueOf(HttpStatus.BAD_REQUEST.value()))) {
                throw new ServiceException("604", "用户名或密码错误");
            } else if (e.getMessage().contains(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))) {
                throw new ServiceException("1600", "网络错误，请检查配置");
            } else {
                throw new ServiceException("1601", "未知错误，请重试");
            }
        }

        // 修改最近登录时间
        userPO.setLastLoginTime(LocalDateTime.now());
        MapperUtils.updateById(oauthUserMapper, userPO);

        log.info("login userId : {} , lastLoginTime:{}", userPO.getId(), userPO.getLastLoginTime());

        // 根据用户Id 获取用户联系方式
        List<OauthUserInfoVO> oauthUserInfoVOS = getUserInfo(userPO.getId());

        //这里拿到了登录成功后返回的token信息之后，再进行一层封装，最后返回给前端的其实是LoginUserVO
        OauthUserVO loginUserVO = new OauthUserVO();
        BeanUtils.copyPropertiesIgnoreNull(userPO, loginUserVO);
        loginUserVO.setAccessToken(token.getAccessToken());
        loginUserVO.setRefreshToken(token.getRefreshToken());
        loginUserVO.setUserId(userPO.getId());
        loginUserVO.setLastLoginTime(userPO.getLastLoginTime());
        loginUserVO.setUsername(userPO.getUsername());
        loginUserVO.setUserInfoVOS(CollectionUtils.isEmpty(oauthUserInfoVOS) ? Lists.newArrayList() : oauthUserInfoVOS);

        convUserInfo(userPO, loginUserVO);

        long duration = TimeUnit.MINUTES.toSeconds(60 * 60 * 12 * 2 * 30);
        //存储登录的用户
        redisUtil.set(("userId:" + userPO.getId()), token.getRefreshToken(), duration);

        redisUtil.set(("userId:" + userPO.getId() + ":access"), token.getAccessToken(), duration);
        redisUtil.set(("userId:" + userPO.getId() + ":refresh"), token.getRefreshToken(), duration);

        return loginUserVO;
    }

    /**
     * @param userPO, loginUserVO
     * @return void 拼接用户数据
     * @author makaiyu
     * @description
     * @date 13:59 2020-02-12
     **/
    private void convUserInfo(OauthUser userPO, OauthUserVO loginUserVO) {
        List<OauthUserInfoVO> oauthUserInfos = getUserInfo(userPO.getId());

        loginUserVO.setUserInfoVOS(CollectionUtils.isEmpty(oauthUserInfos)
                ? Lists.newArrayList() : oauthUserInfos);

        // key: roleId   value: OauthRole
        Map<Long, OauthRole> roleMap = MapperUtils.list(oauthRoleMapper, new QueryWrapper<>())
                .stream().collect(Collectors.toMap(OauthRole::getId, Function.identity()));

        List<OauthRoleVO> oauthRoles = Lists.newArrayListWithCapacity(roleMap.size());
        List<OauthGroupVO> oauthGroupVOS = Lists.newArrayListWithCapacity(roleMap.size());
        List<OauthUserInfoVO> oauthUserInfo = Lists.newArrayListWithCapacity(roleMap.size());
        List<OauthOrganizationVO> oauthOrganizationVOS = Lists.newArrayListWithCapacity(roleMap.size());

        selectUserDetail(userPO, loginUserVO, roleMap, oauthRoles, oauthGroupVOS,
                oauthOrganizationVOS, oauthUserInfo);

        //  获取用户毕业、技能、员工等级
        setUserGraduationAndSkillInfos(loginUserVO, userPO.getStaffLevelId());

        // 查询当前用户直属领导
        List<OauthUserVO> oauthUserVOS = selectUserByUserId(Collections.singletonList(userPO.getDirectLeader()));
        if (!CollectionUtils.isEmpty(oauthUserVOS)) {
            loginUserVO.setDirectLeader(oauthUserVOS.get(0));
        }

        Set<String> permissionCodeByUserId = oauthRoleService.selectPermissionCodeByUserId(userPO.getId());

        loginUserVO.setPermissionCodes(permissionCodeByUserId);

        Set<String> appKeys = oauthPermissionService.selectAppKeyByPermissionCodes(permissionCodeByUserId);

        loginUserVO.setAppKeys(appKeys);

        Integer tenantLevel = tenantCodeMapper.selectTenantLevelById(userPO.getTenantId());
        loginUserVO.setTenantLevel(tenantLevel);

        TenantInfoDTO tenantInfoDTO = new TenantInfoDTO();
        tenantInfoDTO.setTenantId(userPO.getTenantId());
        List<TenantInfoVO> tenantInfos = tenantInfoService.selectTenantInfo(tenantInfoDTO);

        if (!CollectionUtils.isEmpty(tenantInfos)) {
            TenantInfoVO tenantInfoVO = tenantInfos.get(0);
            loginUserVO.setVirtualCurrency(tenantInfoVO.getVirtualCurrency());
        } else {
            loginUserVO.setVirtualCurrency(NumberUtils.LONG_ZERO);
        }

        OauthTenant tenant = MapperUtils.getById(oauthTenantMapper, userPO.getTenantId());
        if (Objects.nonNull(tenant)) {
            loginUserVO.setTenantName(tenant.getTenantName());
        }
    }

    /**
     * @return com.seaboxdata.commons.utils.Resp
     * @author makaiyu
     * @description 退出登录
     * @date 13:59 2019/5/21
     **/
    @Override
    public Boolean logout() {
        try {
            OauthLoginUserVO userDetails = UserUtils.getUserDetails();
            Long userId = userDetails.getUserId();
            String accessToken = (String) redisUtil.getValueByKey("userId:" + userId + ":access");
            String refreshToken = (String) redisUtil.getValueByKey("userId:" + userId + ":refresh");

            /**
             * 删除accessToken
             */
            redisTokenStore.removeAccessToken(accessToken);

            /**
             * 删除refreshToken
             */
            redisTokenStore.removeRefreshToken(refreshToken);

            /**
             * 删除key前缀为userId:的值
             */
            redisUtil.deletePrefixKey("userId:" + userId + ":*");

        } catch (Exception e) {
            log.info("重复注销");
            return true;
        }
        return true;
    }

    /**
     * @param oauthUserDTO
     * @return void
     * @author makaiyu
     * @description 修改用户信息及权限
     * @date 18:56 2019/5/27
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOauthUser(@RequestBody OauthSaveUserDTO oauthUserDTO) {

        if (Objects.isNull(oauthUserDTO)) {
            log.info("updateOauthUser -> param is null");
            throw new ServiceException("400", "修改用户信息参数为空");
        }

        log.info("updateOauthUser -> param is : {} ", oauthUserDTO);

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        try {
            handleUserInfo(oauthUserDTO, tenantId);
        } catch (Exception e) {
            log.warn("修改用户信息错误 : {}", e.getMessage());
            throw new ServiceException("500", "修改用户信息错误");
        }
        return true;
    }

    /**
     * @param userIds
     * @return com.seaboxdata.auth.api.vo.OauthUserVO
     * @author makaiyu
     * @description 根据用户Id  获取用户信息
     * @date 15:59 2019/6/3
     **/
    @Override
    public List<OauthUserVO> selectUserByUserId(@RequestBody List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            log.info("selectUserByUserId -> param userIds is null");
            throw new ServiceException("400", "用户ID为空");
        }

        // 1. 获取用户基本信息
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .in(OauthUser::getId, userIds);
        List<OauthUser> users = MapperUtils.list(oauthUserMapper, wrapper);

        if (CollectionUtils.isEmpty(users)) {
            log.info("selectUserByUserId -> result users is null");
            return Lists.newArrayList();
        }

        List<OauthUserVO> voList = Lists.newArrayListWithCapacity(users.size());
        users.forEach(user -> {
            OauthUserVO oauthUserVO = new OauthUserVO();
            BeanUtils.copyPropertiesIgnoreNull(user, oauthUserVO);
            oauthUserVO.setUserId(user.getId());
            oauthUserVO.setPassword(user.getPassword());
            oauthUserVO.setLastLoginTime(user.getLastLoginTime());

            // 2. 获取分组信息
            List<Long> groupIds = getGroupIds(user.getId());
            List<OauthGroupVO> oauthGroupVOS = Lists.newArrayListWithCapacity(groupIds.size());
            getGroupInfo(user, groupIds, oauthGroupVOS);
            oauthUserVO.setGroups(oauthGroupVOS);

            // 3. 获取角色信息
            List<Long> roleIds = getRoleIds(user.getId());
            List<OauthRoleVO> oauthRoleVOS = Lists.newArrayListWithCapacity(roleIds.size());
            getRoleInfo(roleIds, oauthRoleVOS);
            oauthUserVO.setRoles(oauthRoleVOS);

            // 4. 获取机构信息
            List<Long> organizationIds = getOrganizationIds(user.getId());
            List<OauthOrganizationVO> oauthOrganizationVOS = Lists.newArrayListWithCapacity(groupIds.size());
            getOrganizationInfo(organizationIds, oauthOrganizationVOS);
            oauthUserVO.setOrganizations(oauthOrganizationVOS);

            // 5. 获取用户联系信息
            List<OauthUserInfoVO> oauthUserInfoVOS = getUserInfo(user.getId());
            oauthUserVO.setUserInfoVOS(oauthUserInfoVOS);

            // 6. 获取用户权限码
            Set<String> permissionCodeByUserId = oauthRoleService.selectPermissionCodeByUserId(user.getId());
            oauthUserVO.setPermissionCodes(permissionCodeByUserId);

            // 7. 获取用户毕业、技能、员工等级
            setUserGraduationAndSkillInfos(oauthUserVO, user.getStaffLevelId());

            // 8. 查询当前用户直属领导
            List<OauthUserVO> oauthUserVOS = selectUserByUserId(Collections.singletonList(user.getDirectLeader()));
            if (!CollectionUtils.isEmpty(oauthUserVOS)) {
                oauthUserVO.setDirectLeader(oauthUserVOS.get(0));
            }

            voList.add(oauthUserVO);
        });
        return voList;
    }


    /**
     * @param roleId
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据roleId  寻找所属角色下的人员信息
     * @date 18:50 2019/6/3
     **/
    @Override
    public List<OauthUserVO> selectUserByRoleId(Long roleId) {

        if (Objects.isNull(roleId)) {
            log.info("selectUserByRoleId -> param roleId is null");
            throw new ServiceException("400", "查询角色-人员时,角色Id为空");
        }

        log.info("selectUserByRoleId -> roleId : {} ", roleId);

        // 1. 拿到角色下对应的 userIds
        LambdaQueryWrapper<OauthUserRole> wrapper = new QueryWrapper<OauthUserRole>().lambda()
                .eq(OauthUserRole::getRoleId, roleId);
        List<Long> userIds = MapperUtils.list(oauthUserRoleMapper, wrapper)
                .stream().map(OauthUserRole::getUserId).collect(toList());

        List<OauthUserVO> oauthUserVOS = Lists.newArrayListWithCapacity(userIds.size());

        // 2. 拿到每个user 获取user info
        if (!CollectionUtils.isEmpty(userIds)) {
            LambdaQueryWrapper<OauthUser> userWrapper = new QueryWrapper<OauthUser>().lambda()
                    .in(OauthUser::getId, userIds);

            List<OauthUser> users = MapperUtils.list(oauthUserMapper, userWrapper);
            if (!CollectionUtils.isEmpty(users)) {
                users.forEach(user -> {
                    OauthUserVO oauthUserVO = new OauthUserVO();
                    BeanUtils.copyPropertiesIgnoreNull(user, oauthUserVO);
                    oauthUserVO.setUserId(user.getId());

                    LambdaQueryWrapper<OauthUserRole> roleWrapper = new QueryWrapper<OauthUserRole>().lambda()
                            .eq(OauthUserRole::getUserId, user.getId());
                    List<Long> roleIds = MapperUtils.list(oauthUserRoleMapper, roleWrapper)
                            .stream().map(OauthUserRole::getRoleId).collect(toList());

                    LambdaQueryWrapper<OauthRole> roleNameWrapper = new QueryWrapper<OauthRole>().lambda()
                            .in(OauthRole::getId, roleIds);
                    String roleNames = MapperUtils.list(oauthRoleMapper, roleNameWrapper)
                            .stream().map(OauthRole::getRoleName).collect(Collectors.joining(","));

                    oauthUserVO.setRoleName(roleNames);
                    oauthUserVOS.add(oauthUserVO);
                });
            }
        }

        return oauthUserVOS;
    }

    /**
     * @return java.lang.Long
     * @author makaiyu
     * @description 获取当前登陆用户信息
     * @date 9:57 2019/6/6
     **/
    @Override
    public OauthUserVO getLoginUser() {

        OauthLoginUserVO userVO = UserUtils.getUserDetails();

        log.info("getLoginUser userVO : {} ", userVO);

        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getId, userVO.getUserId());

        OauthUser oauthUser = MapperUtils.getOne(oauthUserMapper, wrapper, true);

        if (Objects.isNull(oauthUser)) {
            return new OauthUserVO();
        }

        OauthUserVO oauthUserVO = new OauthUserVO();
        BeanUtils.copyPropertiesIgnoreNull(oauthUser, oauthUserVO);
        oauthUserVO.setUserId(oauthUser.getId());
        oauthUserVO.setLastLoginTime(oauthUser.getLastLoginTime());

        // 拼接用户信息
        convUserInfo(oauthUser, oauthUserVO);
        return oauthUserVO;
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断username 是否存在
     * @date 14:41 2019/8/9
     **/
    @Override
    public Boolean checkUserName(@RequestBody OauthUserDTO user) {

        if (StringUtils.isEmpty(user.getUsername())) {
            throw new ServiceException("400", "校验userName为null");
        }

        // 1. 当用户新建时，只接受username 判断是否存在
        // 2. 当用户编辑时，先判断username是否存在，然后判断是否与传入的userId 相同
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getUsername, user.getUsername());

        OauthUser oauthUser = MapperUtils.getOne(oauthUserMapper, wrapper, true);

        if (Objects.nonNull(oauthUser) && oauthUser.getId().equals(user.getId())) {
            return true;
        }

        return oauthUser == null;
    }

    /**
     * @param authUserParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据姓名或工号查询人员
     * @date 9:29 2019/8/14
     **/
    @Override
    public List<OauthUserVO> authSelectUserByNameOrCard(@RequestBody AuthUserParamDTO authUserParamDTO) {

        Long tenantId;

        if (Objects.nonNull(authUserParamDTO.getTenantId())) {
            tenantId = authUserParamDTO.getTenantId();
        } else {
            OauthLoginUserVO userDetails = UserUtils.getUserDetails();
            tenantId = userDetails.getTenantId();
        }

        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>()
                .lambda().eq(OauthUser::getTenantId, tenantId);
        if (!StringUtils.isEmpty(authUserParamDTO.getKeyWords())) {
            wrapper.like(OauthUser::getJobNumber, authUserParamDTO.getKeyWords())
                    .or()
                    .like(OauthUser::getName, authUserParamDTO.getKeyWords());
        }

        List<OauthUser> userList = MapperUtils.list(oauthUserMapper, wrapper);

        List<OauthUserVO> userVOList = Lists.newArrayListWithCapacity(userList.size());

        userList.forEach(oauthUser -> {
            OauthUserVO oauthUserVO = new OauthUserVO();
            BeanUtils.copyPropertiesIgnoreNull(oauthUser, oauthUserVO);
            oauthUserVO.setUserId(oauthUser.getId());
            userVOList.add(oauthUserVO);
        });

        return userVOList;
    }

    /**
     * @param userIds
     * @return OauthUserDTO
     * @author zdl
     * @description 根据userIds 获取List<OauthUserDTO></>对象, 仅仅查询oauth_user的数据，不关联其他信息
     * @date 10:00 2019/8/23
     **/
    @Override
    public List<OauthUserDTO> queryUsersByIds(@RequestBody List<Long> userIds) {
        LambdaQueryWrapper<OauthUser> userWrapper = new QueryWrapper<OauthUser>().lambda().in(OauthUser::getId, userIds);
        List<OauthUser> users = MapperUtils.list(oauthUserMapper, userWrapper);
        List<OauthUserDTO> userDTOList = users.stream().map(user -> {
            OauthUserDTO userDTO = new OauthUserDTO();
            BeanUtils.copyPropertiesIgnoreNull(user, userDTO);
            userDTO.setPassword(null);
            return userDTO;
        }).collect(toList());

        return userDTOList;
    }

    /**
     * @param pwdDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改用户密码
     * @date 9:09 2019/9/26
     **/
    @Override
    public Boolean updateUserPwd(@RequestBody PwdDTO pwdDTO) {

        if (Objects.isNull(pwdDTO.getNewPwd()) || Objects.isNull(pwdDTO.getOriginalPwd())) {
            return false;
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();

        OauthUser oauthUser = MapperUtils.getById(oauthUserMapper, userDetails.getUserId());

        String pwd = "";

        if (Objects.nonNull(oauthUser)) {
            pwd = oauthUser.getPassword();
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        // 若比对成功
        if (bCryptPasswordEncoder.matches(pwdDTO.getOriginalPwd(), pwd)) {
            oauthUser.setPassword(EncryptUtil.encryptPassword(pwdDTO.getNewPwd()));
            return MapperUtils.updateById(oauthUserMapper, oauthUser);
        }

        return false;
    }

    /**
     * @param user
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 为用户赋角色
     * @date 17:07 2019/11/22
     **/
    @Override
    public Boolean updateUserRoleByUserId(OauthSaveUserDTO user) {

        if (Objects.isNull(user) || Objects.isNull(user.getId())) {
            log.info("updateUserRoleByUserId param is null or userId is null");
            return false;
        }

        OauthUser oauthUser = new OauthUser();
        oauthUser.setId(user.getId());
        oauthUser.setUpdateTime(LocalDateTime.now());

        boolean flag = true;

        // 为用户赋角色
        try {
            setRoleForUser(user, oauthUser);
        } catch (Exception e) {
            log.error("updateUserRoleByUserId ->修改用户对应角色失败");
            flag = false;
        }

        return flag;
    }

    /**
     * @param userNames
     * @return java.lang.Boolean
     * @author makaiyu
     * @description
     * @date 14:11 2019-12-09
     **/
    @Override
    public Boolean deleteUserByUsername(List<String> userNames) {

        if (CollectionUtils.isEmpty(userNames)) {
            log.info("deleteUserByUsername -> param is null");
            return false;
        }

        log.info("deleteUserByUsername -> param is : {}", userNames);

        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .in(OauthUser::getUsername, userNames)
                .eq(OauthUser::getSystemUser, false);

        return MapperUtils.remove(oauthUserMapper, wrapper);
    }

    /**
     * @param addressDTO
     * @return void
     * @author makaiyu
     * @description 保存用户登陆日志
     * @date 14:38 2020-01-02
     **/
    @Override
    public void saveAddressLog(AddressDTO addressDTO) {
        OauthAddressLog oauthAddressLog = new OauthAddressLog();
        BeanUtils.copyPropertiesIgnoreNull(addressDTO, oauthAddressLog);

        OauthUserVO oauthUserVO = selectUserByUserId(Lists.newArrayList(addressDTO.getUserId())).get(0);

        oauthAddressLog.setUserId(addressDTO.getUserId())
                .setTenantId(addressDTO.getTenantId())
                .setUserName(oauthUserVO.getUsername())
                .setUpdateTime(LocalDateTime.now());

        MapperUtils.saveOrUpdate(oauthAddressLogMapper, oauthAddressLog);

        log.debug("保存登陆日志:{}", oauthAddressLog);
    }

    /**
     * @param userId
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据用户Id 退出用户
     * @date 09:59 2020-04-07
     **/
    @Override
    public Boolean logoutByUserId(Long userId) {
        try {
            String accessToken = (String) redisUtil.getValueByKey("userId:" + userId + ":access");
            String refreshToken = (String) redisUtil.getValueByKey("userId:" + userId + ":refresh");

            /**
             * 删除accessToken
             */
            redisTokenStore.removeAccessToken(accessToken);

            /**
             * 删除refreshToken
             */
            redisTokenStore.removeRefreshToken(refreshToken);

            /**
             * 删除key前缀为userId:的值
             */
            redisUtil.deletePrefixKey("userId:" + userId + ":*");

        } catch (Exception e) {
            log.info("重复注销");
            return true;
        }
        return true;
    }


    /**
     * @param userId, roleIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个角色
     * @date 14:36 2020-04-26
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateRoleUser(Long userId, @RequestBody List<Long> roleIds) {
        if (Objects.isNull(userId) || CollectionUtils.isEmpty(roleIds)) {
            log.info("saveOrUpdateUserRole -> param is null!");
            return false;
        }

        Long tenantId = UserUtils.getUserDetails().getTenantId();
        OauthSaveUserDTO oauthSaveUserDTO = new OauthSaveUserDTO();
        oauthSaveUserDTO.setRoleIds(roleIds)
                .setTenantId(tenantId)
                .setId(userId);

        try {
            setRoleForUser(oauthSaveUserDTO, new OauthUser().setId(userId));
        } catch (Exception e) {
            log.error("saveOrUpdateUserRole ->", e);
            throw new ServiceException("500", "保存 用户角色关系 失败");
        }

        return true;
    }


    /**
     * @param userId, groupIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个分组
     * @date 14:36 2020-04-26
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateGroupUser(Long userId, @RequestBody List<Long> groupIds) {
        if (Objects.isNull(userId) || CollectionUtils.isEmpty(groupIds)) {
            log.info("saveOrUpdateUserGroup -> param is null!");
            return false;
        }
        Long tenantId = UserUtils.getUserDetails().getTenantId();

        OauthSaveUserDTO oauthSaveUserDTO = new OauthSaveUserDTO();
        oauthSaveUserDTO.setGroupIds(groupIds)
                .setTenantId(tenantId)
                .setId(userId);
        try {
            setGroupForUser(oauthSaveUserDTO, UserUtils.getUserDetails().getTenantId(),
                    new OauthUser().setId(userId));
        } catch (Exception e) {
            log.error("saveOrUpdateUserGroup ->", e);
            throw new ServiceException("500", "保存 用户分组关系 失败");
        }

        return true;
    }


    /**
     * @param pageDTO
     * @return com.seaboxdata.commons.query.PaginationResult<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 内部管理系统获取全部用户
     * @date 13:25 2020-05-11
     **/
    @Override
    public PaginationJxpmUserResult<OauthJxpmUserVO> selectJxpmAllUser(OauthUserNamePageDTO pageDTO) {

        if (Objects.isNull(pageDTO)) {
            return new PaginationJxpmUserResult<>();
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();
        List<Long> groupUserIds = Lists.newArrayList();
        List<Long> organizationUserIds = Lists.newArrayList();

        if (Objects.nonNull(pageDTO.getGroupId())) {
            groupUserIds = getGroupsForUser(pageDTO);
        }

        if (Objects.nonNull(pageDTO.getOrganizationId())) {
            organizationUserIds = getOrganizationUserIds(pageDTO, tenantId);
        }

        // 1. 根据租户ID 获取全部用户
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getTenantId, tenantId);

        // 更具员工等级过滤，如果存在员工等级id
        if (Objects.nonNull(pageDTO.getStaffLevelId())) {
            wrapper.eq(OauthUser::getStaffLevelId, pageDTO.getStaffLevelId());
        }

        List<Long> allUserIds = Lists.newArrayList();
        allUserIds.addAll(groupUserIds);
        allUserIds.addAll(organizationUserIds);

        if (allUserIds.size() == NumberUtils.INTEGER_ZERO && (Objects.nonNull(pageDTO.getGroupId())
                || Objects.nonNull(pageDTO.getOrganizationId()))) {
            return new PaginationJxpmUserResult<>();
        }

        convUserWrapper(pageDTO, wrapper, allUserIds);

        List<OauthUser> oauthUsers = Lists.newArrayList();

        List<OauthJxpmUserVO> oauthJxpmUserVOS = Lists.newArrayList();

        // 员工等级设置
        Map<Long, StaffLevel> levelMap = Maps.newHashMap();

        getUserLevel(oauthUsers, levelMap);

        // 直属领导设置
        Map<Long, OauthUser> managerUserMap = Maps.newHashMapWithExpectedSize(oauthUsers.size());

        getUserLeader(oauthUsers, managerUserMap);

        Page<OauthUser> userPage = null;
        // 如果不为分页  则获取全部
        if (Objects.isNull(pageDTO.getOffset()) && Objects.isNull(pageDTO.getLimit())) {
            oauthUsers = MapperUtils.list(oauthUserMapper, wrapper);
            List<OauthJxpmUserVO> finalOauthUserVOS = oauthJxpmUserVOS;

            convJxpmUser(levelMap, managerUserMap, finalOauthUserVOS, oauthUsers);
            oauthJxpmUserVOS = finalOauthUserVOS;
        } else {
            userPage = (Page<OauthUser>) MapperUtils.page(oauthUserMapper,
                    new Page<>(pageDTO.getOffset(), pageDTO.getLimit()), wrapper);

            List<OauthJxpmUserVO> finalOauthUserVOS = oauthJxpmUserVOS;

            convJxpmUser(levelMap, managerUserMap, finalOauthUserVOS, userPage.getRecords());
            oauthJxpmUserVOS = finalOauthUserVOS;
        }

        // 2. 封装Page对象
        PaginationJxpmUserResult<OauthJxpmUserVO> paginationResult = new PaginationJxpmUserResult<>();
        paginationResult.setJxpmUsers(CollectionUtils.isEmpty(oauthJxpmUserVOS) ? Lists.newArrayList() : oauthJxpmUserVOS);
        if (Objects.nonNull(userPage)) {
            paginationResult.setTotal((int) userPage.getTotal());
            paginationResult.setLimit((int) userPage.getSize());
        } else {
            paginationResult.setTotal(oauthUsers.size());
        }
        paginationResult.setOffset(pageDTO.getOffset());
        return paginationResult;
    }

    /**
     * @param pageDTO
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 获取Groups
     * @date 16:31 2020-05-11
     **/
    private List<Long> getGroupsForUser(OauthUserNamePageDTO pageDTO) {
        List<Long> groupUserIds;// 判断分组Id是否为空
        LambdaQueryWrapper<OauthUserGroup> groupWrapper = new QueryWrapper<OauthUserGroup>().lambda();
        groupWrapper.eq(OauthUserGroup::getGroupId, pageDTO.getGroupId());
        groupUserIds = MapperUtils.list(oauthUserGroupMapper, groupWrapper)
                .stream().map(OauthUserGroup::getUserId).collect(toList());
        return groupUserIds;
    }

    /**
     * @param pageDTO, wrapper, allUserIds
     * @return void
     * @author makaiyu
     * @description 拼接UserWrapper
     * @date 16:31 2020-05-11
     **/
    private void convUserWrapper(OauthUserNamePageDTO pageDTO, LambdaQueryWrapper<OauthUser> wrapper, List<Long> allUserIds) {
        if (!CollectionUtils.isEmpty(allUserIds)) {
            wrapper.in(OauthUser::getId, allUserIds);
        }

        if (!StringUtils.isEmpty(pageDTO.getName())) {
            wrapper.like(OauthUser::getName, pageDTO.getName());
        }
    }

    /**
     * @param oauthUsers,graduationInfoMap
     * @param graduationInfoMap
     * @return void
     * @author makaiyu
     * @description 获取用户毕业相关信息
     * @date 16:28 2020-05-11
     **/
    private void getUserGraduation(List<OauthUser> oauthUsers, Map<Long, List<GraduationInfo>> graduationInfoMap) {
        List<Long> userIds = oauthUsers.stream().map(OauthUser::getId).collect(toList());

        List<GraduationInfo> graduationInfos = graduationInfoService.list();

        userIds.forEach(userId -> {
            List<GraduationInfo> infos = graduationInfos.stream().filter(graduationInfo ->
                    userId.equals(graduationInfo.getUserId())).collect(toList());

            graduationInfoMap.put(userId, infos);
        });
    }

    /**
     * @param oauthUsers,userSkillMap
     * @param userSkillMap
     * @return void
     * @author makaiyu
     * @description 获取用户直属领导
     * @date 16:28 2020-05-11
     **/
    private void getUserSkill(List<OauthUser> oauthUsers, Map<Long, List<SkillInfo>> userSkillMap) {
        List<Long> userIds = oauthUsers.stream().map(OauthUser::getId).collect(toList());

        List<SkillInfo> skillInfos = skillInfoService.list();

        userIds.forEach(userId -> {
            List<SkillInfo> infos = skillInfos.stream().filter(graduationInfo ->
                    userId.equals(graduationInfo.getUserId())).collect(toList());

            userSkillMap.put(userId, infos);
        });
    }

    /**
     * @param oauthUsers, managerUserMap
     * @return void
     * @author makaiyu
     * @description 获取用户直属领导
     * @date 16:28 2020-05-11
     **/
    private void getUserLeader(List<OauthUser> oauthUsers, Map<Long, OauthUser> managerUserMap) {
        Map<Long, Long> leaderIds = oauthUsers.stream().collect(toMap(OauthUser::getId, OauthUser::getDirectLeader));

        List<OauthUser> users = MapperUtils.list(oauthUserMapper, new QueryWrapper<>());

        leaderIds.forEach((userId, leaderId) -> {
            Optional<OauthUser> optional = users.stream()
                    .filter(oauthUser -> oauthUser.getId().equals(leaderId)).findFirst();

            if (optional.isPresent()) {
                OauthUser oauthUser = optional.get();
                managerUserMap.put(userId, oauthUser);
            }
        });
    }

    /**
     * @param oauthUsers, levelMap 获取员工级别
     * @return void
     * @author makaiyu
     * @description
     * @date 16:21 2020-05-11
     **/
    private void getUserLevel(List<OauthUser> oauthUsers, Map<Long, StaffLevel> levelMap) {
        Map<Long, Long> ids = oauthUsers.stream().collect(toMap(OauthUser::getId, OauthUser::getStaffLevelId));

        List<StaffLevel> staffLevels = staffLevelService.list();

        ids.forEach((userId, levelId) -> {
            Optional<StaffLevel> optional = staffLevels.stream()
                    .filter(staffLevel -> staffLevel.getId().equals(levelId)).findFirst();

            if (optional.isPresent()) {
                StaffLevel staffLevel = optional.get();
                levelMap.put(userId, staffLevel);
            }
        });
    }

    /**
     * @param levelMap, managerUserMap, finalOauthUserVOS, records
     * @return void
     * @author makaiyu
     * @description 拼装jxpm获取用户
     * @date 16:19 2020-05-11
     **/
    private void convJxpmUser(Map<Long, StaffLevel> levelMap, Map<Long, OauthUser> managerUserMap,
                              List<OauthJxpmUserVO> finalOauthUserVOS, List<OauthUser> records) {
        records.forEach(user -> {
            OauthJxpmUserVO oauthJxpmUserVO = new OauthJxpmUserVO();

            org.springframework.beans.BeanUtils.copyProperties(user, oauthJxpmUserVO);
            oauthJxpmUserVO.setUserId(user.getId());
            managerUserMap.forEach((userId, oauthUser) -> {
                if (user.getId().equals(userId)) {
                    OauthUserVO oauthUserVO = new OauthUserVO();
                    org.springframework.beans.BeanUtils.copyProperties(oauthUser, oauthUserVO);
                    oauthUserVO.setUserId(userId);
                    oauthJxpmUserVO.setDirectLeader(oauthUserVO);
                }
            });

            levelMap.forEach((userId, staffLevel) -> {
                if (user.getId().equals(userId)) {
                    oauthJxpmUserVO.setStaffLevelVo(staffLevelConverter.toStaffLevelVo(staffLevel));
                }
            });
            finalOauthUserVOS.add(oauthJxpmUserVO);
        });
    }


    /**
     * @param oauthUser,           oauthUserVO, roleMap, oauthRoles, oauthGroupVOS，oauthUserInfoVOs
     * @param oauthOrganizationVOS
     * @return void
     * @author makaiyu
     * @description 根据用户信息 获取用户角色及分组
     * @date 17:41 2019/6/17
     **/
    private void selectUserDetail(OauthUser oauthUser, OauthUserVO oauthUserVO,
                                  Map<Long, OauthRole> roleMap, List<OauthRoleVO> oauthRoles,
                                  List<OauthGroupVO> oauthGroupVOS, List<OauthOrganizationVO> oauthOrganizationVOS,
                                  List<OauthUserInfoVO> oauthUserInfoVOs) {
        // 获取该用户下 所有角色Id
        List<Long> roleIds = getRoleIds(oauthUser.getId());
        List<String> roleNames = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(roleIds)) {
            roleIds.forEach(roleId -> {
                if (roleMap.containsKey(roleId)) {
                    OauthRole oauthRole = roleMap.get(roleId);
                    OauthRoleVO roleVO = new OauthRoleVO();
                    roleNames.add(oauthRole.getRoleName());
                    BeanUtils.copyPropertiesIgnoreNull(oauthRole, roleVO);
                    oauthRoles.add(roleVO);
                }
            });
            oauthUserVO.setRoles(oauthRoles);

            String roleName = String.join(",", roleNames);
            oauthUserVO.setRoleName(roleName);
        }

        // 获取该用户下 所有group分组信息
        List<OauthGroup> groups = oauthUserGroupMapper.selectGroupByUserId(oauthUser.getId(), oauthUser.getTenantId());
        if (!CollectionUtils.isEmpty(groups)) {
            groups.forEach(group -> {
                OauthGroupVO oauthGroupVO = new OauthGroupVO();
                oauthGroupVO.setManagerName(oauthUser.getUsername());
                oauthGroupVO.setManagerPhone(oauthUser.getPhoneNumber());
                oauthGroupVO.setManagerMail(oauthUser.getEmail());
                if (Objects.nonNull(group)) {
                    BeanUtils.copyPropertiesIgnoreNull(group, oauthGroupVO);
                    oauthGroupVO.setGroupId(group.getId());
                }
                oauthGroupVOS.add(oauthGroupVO);
            });
        }
        oauthUserVO.setGroups(oauthGroupVOS);

        // 获取该用户联系方式
        List<OauthUserInfo> userInfos = oauthUserInfoMapper.selectUserInfoByUserId(oauthUser.getId());

        if (!CollectionUtils.isEmpty(userInfos)) {
            userInfos.forEach(userInfo -> {
                OauthUserInfoVO oauthUserInfoVO = new OauthUserInfoVO();
                BeanUtils.copyPropertiesIgnoreNull(userInfo, oauthUserInfoVO);
                oauthUserInfoVO.setContact(userInfo.getContact());
                oauthUserInfoVOs.add(oauthUserInfoVO);
            });
        }
        oauthUserVO.setUserInfoVOS(oauthUserInfoVOs);

        // 获取该用户下 所有的机构信息
        List<OauthOrganization> oauthOrganizations = oauthUserOrganizationMapper
                .selectOrganizationUserId(oauthUser.getId(), oauthUser.getTenantId());

        Set<Long> managerUserIds = oauthOrganizations.stream().map(OauthOrganization::getManagerUserId).collect(Collectors.toSet());

        Map<Long, OauthUserVO> userMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(managerUserIds)) {
            List<OauthUserVO> userVOS = oauthUserService.selectUserByUserId(Lists.newArrayList(managerUserIds));
            userMap = userVOS.stream().collect(toMap(OauthUserVO::getUserId, Function.identity()));
        }

        if (!CollectionUtils.isEmpty(oauthOrganizations)) {
            Map<Long, OauthUserVO> finalUserMap = userMap;
            oauthOrganizations.forEach(organization -> {

                OauthOrganizationVO organizationVO = new OauthOrganizationVO();
                organizationVO.setManagerPhone(oauthUser.getPhoneNumber());
                organizationVO.setManagerMail(oauthUser.getEmail());
                if (Objects.nonNull(organization) && !CollectionUtils.isEmpty(finalUserMap)) {
                    finalUserMap.forEach((userId, userVO) -> {
                        if (organization.getManagerUserId().equals(userId)) {
                            organizationVO.setManagerName(userVO.getUsername());
                        }
                    });

                    BeanUtils.copyPropertiesIgnoreNull(organization, organizationVO);
                    organizationVO.setOrganizationId(organization.getId());
                }
                oauthOrganizationVOS.add(organizationVO);
            });
        }
        oauthUserVO.setOrganizations(oauthOrganizationVOS);
    }

    /**
     * @param paramMap, token
     * @return void
     * @author makaiyu
     * @description 转发到oauth2 内部校验接口
     * @date 13:48 2019/5/21
     **/
    private void tokenHandler(MultiValueMap<String, Object> paramMap, Token token) throws
            ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Map<String, Object> countryMap = refreshTokenUtils.handleTokenModel(paramMap);
        DataHelper.putDataIntoEntity(countryMap, token);
        log.info("token:{}", token);
    }

    /**
     * @param userId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据用户Id  获取机构Ids
     * @date 18:54 2019/7/24
     **/
    private List<Long> getOrganizationIds(Long userId) {

        if (Objects.isNull(userId)) {
            log.info("getOrganizationIds -> param userId is null");
            return Lists.newArrayList();
        }

        LambdaQueryWrapper<OauthUserOrganization> wrapper = new QueryWrapper<OauthUserOrganization>().lambda()
                .eq(OauthUserOrganization::getUserId, userId);
        return MapperUtils.list(oauthUserOrganizationMapper, wrapper)
                .stream().map(OauthUserOrganization::getOrganizationId).collect(toList());
    }

    /**
     * @param userId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据用户Id  获取角色Ids
     * @date 13:35 2019/6/17
     **/
    private List<Long> getRoleIds(Long userId) {

        if (Objects.isNull(userId)) {
            log.info("getRoleIds -> param userId is null");
            return Lists.newArrayList();
        }

        LambdaQueryWrapper<OauthUserRole> wrapper = new QueryWrapper<OauthUserRole>().lambda()
                .eq(OauthUserRole::getUserId, userId);
        return MapperUtils.list(oauthUserRoleMapper, wrapper)
                .stream().map(OauthUserRole::getRoleId).collect(toList());
    }

    /**
     * @param groupIds
     * @return java.util.List<com.seaboxdata.auth.server.model.OauthGroup>
     * @author makaiyu
     * @description 根据分组Id  获取分组信息
     * @date 16:46 2019/6/3
     **/
    private List<OauthGroup> getGroupsByIds(List<Long> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            log.info("getGroupsByIds -> param groupIds is null");
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<OauthGroup> query = new QueryWrapper<OauthGroup>().lambda()
                .in(OauthGroup::getId, groupIds);

        return MapperUtils.list(oauthGroupMapper, query);
    }

    /**
     * @param organizationIds
     * @return java.util.List<com.seaboxdata.auth.server.model.OauthOrganization>
     * @author makaiyu
     * @description 根据机构Id  获取机构信息
     * @date 18:56 2019/7/24
     **/
    private List<OauthOrganization> getOrganizationByIds(List<Long> organizationIds) {
        if (CollectionUtils.isEmpty(organizationIds)) {
            log.info("getDeptByDeptIds -> param deptIds is null");
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<OauthOrganization> query = new QueryWrapper<OauthOrganization>().lambda()
                .in(OauthOrganization::getId, organizationIds);

        return MapperUtils.list(oauthOrganizationMapper, query);
    }


    /**
     * @param userId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据UserId 获取其分组ID
     * @date 16:44 2019/6/3
     **/
    private List<Long> getGroupIds(Long userId) {
        if (Objects.isNull(userId)) {
            log.info("getGroupIds -> param userId is null");
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<OauthUserGroup> queryWrapper = new QueryWrapper<OauthUserGroup>().lambda()
                .eq(OauthUserGroup::getUserId, userId);
        return MapperUtils.list(oauthUserGroupMapper, queryWrapper)
                .stream().map(OauthUserGroup::getGroupId).collect(toList());
    }

    /**
     * @param user
     * @param tenantId
     * @return void
     * @author makaiyu
     * @description 新增用户 / 修改用户处理
     * @date 15:40 2019/5/30
     **/
    @Override
    public Long handleUserInfo(OauthSaveUserDTO user, Long tenantId) {
        // 1. 注册用户到user表中
        OauthUser oauthUser = new OauthUser();
        oauthUser.setUpdateTime(LocalDateTime.now());
        oauthUser.setTenantId(tenantId);
        BeanUtils.copyPropertiesIgnoreNull(user, oauthUser);
        // 获取员工等级
        if (Objects.nonNull(user.getStaffLevelDTO())) {
            oauthUser.setStaffLevelId(user.getStaffLevelDTO().getId());
        }

        log.info("pwd : {} ", user.getPassword());

        if (!Objects.isNull(user.getPassword())) {
            oauthUser.setPassword(EncryptUtil.encryptPassword(user.getPassword()));
        }

        MapperUtils.saveOrUpdate(oauthUserMapper, oauthUser);

        // 2. 为新用户加入用户联系方式
        setUserInfoForUser(user, oauthUser);

        // 3. 为新用户赋角色
        setRoleForUser(user, oauthUser);

        // 4. 为新用户分组
        setGroupForUser(user, tenantId, oauthUser);

        // 5. 为新用户加机构
        setOrganizationForUser(user, tenantId, oauthUser);

        // 如果userDto 中没有主键id 则是新建用户
        if (Objects.isNull(user.getId())) {

            // 6. 添加用户毕业相关信息
            if (!CollectionUtils.isEmpty(user.getGraduationInfos())) {
                graduationInfoService.saveBatch(graduationConverter.toGraduationInfoList(user.getGraduationInfos())
                        .stream()
                        .peek(graduationInfo -> graduationInfo.setCreator(UserUtils.getUserDetails().getUserId())
                                .setCreateTime(LocalDateTime.now())
                                .setUserId(oauthUser.getId()))
                        .collect(toList()));
            }

            // 7.添加技能相关信息
            if (!CollectionUtils.isEmpty(user.getSkillInfos())) {
                skillInfoService.saveBatch(skillInfoConverter.toSkillInfoList(user.getSkillInfos())
                        .stream()
                        .peek(skillInfo -> skillInfo.setCreator(UserUtils.getUserDetails().getUserId())
                                .setCreateTime(LocalDateTime.now())
                                .setUserId(oauthUser.getId()))
                        .collect(toList()));
            }
        }

        return oauthUser.getId();
    }

    /**
     * @param user, oauthUser
     * @return void
     * @author makaiyu
     * @description 为新用户加入用户联系方式
     * @date 11:06 2019/8/15
     **/
    private void setUserInfoForUser(OauthSaveUserDTO user, OauthUser oauthUser) {
        LambdaQueryWrapper<OauthUserInfo> userInfoWrapper = new QueryWrapper<OauthUserInfo>().lambda()
                .eq(OauthUserInfo::getUserId, user.getId());
        MapperUtils.remove(oauthUserInfoMapper, userInfoWrapper);

        oauthUser.setEmail(org.apache.commons.lang3.StringUtils.EMPTY)
                .setFaxNumber(org.apache.commons.lang3.StringUtils.EMPTY)
                .setFixedPhoneNumber(org.apache.commons.lang3.StringUtils.EMPTY)
                .setPhoneNumber(org.apache.commons.lang3.StringUtils.EMPTY);

        MapperUtils.updateById(oauthUserMapper, oauthUser);

        List<OauthUserInfoDTO> oauthUserInfos = user.getOauthUserInfos();
        if (!CollectionUtils.isEmpty(oauthUserInfos)) {
            oauthUserInfos.forEach(userInfo -> {
                userInfo.setUserId(oauthUser.getId());
                OauthUserInfo oauthUserInfo = new OauthUserInfo();
                BeanUtils.copyPropertiesIgnoreNull(userInfo, oauthUserInfo);
                MapperUtils.saveOrUpdate(oauthUserInfoMapper, oauthUserInfo);

                // 若为主联系方式 则加入oauth_user表中
                if (userInfo.getIsPrimary()) {
                    switch (userInfo.getContact()) {
                        case MAILBOX:
                            oauthUser.setEmail(userInfo.getInformation());
                            MapperUtils.updateById(oauthUserMapper, oauthUser);
                            break;
                        case FAXNUMBER:
                            oauthUser.setFaxNumber(userInfo.getInformation());
                            MapperUtils.updateById(oauthUserMapper, oauthUser);
                            break;
                        case FIXEDTELEPHONE:
                            oauthUser.setFixedPhoneNumber(userInfo.getInformation());
                            MapperUtils.updateById(oauthUserMapper, oauthUser);
                            break;
                        case MOBILEPHONE:
                            oauthUser.setPhoneNumber(userInfo.getInformation());
                            MapperUtils.updateById(oauthUserMapper, oauthUser);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    /**
     * @param user, oauthUser
     * @return void
     * @author makaiyu
     * @description 为新用户赋角色
     * @date 11:04 2019/8/15
     **/
    private void setRoleForUser(OauthSaveUserDTO user, OauthUser oauthUser) {
        LambdaQueryWrapper<OauthUserRole> userRoleWrapper = new QueryWrapper<OauthUserRole>().lambda()
                .eq(OauthUserRole::getUserId, user.getId());
        MapperUtils.remove(oauthUserRoleMapper, userRoleWrapper);
        if (!CollectionUtils.isEmpty(user.getRoleIds())) {
            user.getRoleIds().forEach(role -> {
                MapperUtils.saveOrUpdate(oauthUserRoleMapper, new OauthUserRole()
                        .setUpdateTime(LocalDateTime.now())
                        .setUserId(oauthUser.getId())
                        .setRoleId(role));
            });
        }
    }

    /**
     * @param user, tenantId, oauthUser
     * @return void
     * @author makaiyu
     * @description 为新用户分组
     * @date 11:04 2019/8/15
     **/
    private void setGroupForUser(OauthSaveUserDTO user, Long tenantId, OauthUser oauthUser) {
        LambdaQueryWrapper<OauthUserGroup> userGroupWrapper = new QueryWrapper<OauthUserGroup>().lambda()
                .eq(OauthUserGroup::getUserId, user.getId());
        MapperUtils.remove(oauthUserGroupMapper, userGroupWrapper);
        if (!CollectionUtils.isEmpty(user.getGroupIds())) {
            user.getGroupIds().forEach(group -> {
                MapperUtils.saveOrUpdate(oauthUserGroupMapper, new OauthUserGroup()
                        .setUpdateTime(LocalDateTime.now())
                        .setUserId(oauthUser.getId())
                        .setTenantId(tenantId)
                        .setGroupId(group));
            });
        }
    }

    /**
     * @param user, tenantId, oauthUser
     * @return void
     * @author makaiyu
     * @description 为新用户加入机构
     * @date 11:03 2019/8/15
     **/
    private void setOrganizationForUser(OauthSaveUserDTO user, Long tenantId, OauthUser oauthUser) {
        LambdaQueryWrapper<OauthUserOrganization> userOrganizationWrapper = new QueryWrapper<OauthUserOrganization>().lambda()
                .eq(OauthUserOrganization::getUserId, user.getId());
        MapperUtils.remove(oauthUserOrganizationMapper, userOrganizationWrapper);
        if (!CollectionUtils.isEmpty(user.getOrganizationIds())) {
            user.getOrganizationIds().forEach(organizationId -> {
                MapperUtils.saveOrUpdate(oauthUserOrganizationMapper, new OauthUserOrganization()
                        .setUpdateTime(LocalDateTime.now())
                        .setTenantId(tenantId)
                        .setOrganizationId(organizationId)
                        .setUserId(oauthUser.getId()));
            });
        }
    }

    /**
     * @param userId
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserInfoVO>
     * @author makaiyu
     * @description 根据用户Id获取用户信息
     * @date 10:51 2019/8/15
     **/
    private List<OauthUserInfoVO> getUserInfo(Long userId) {

        List<OauthUserInfo> infoList = oauthUserInfoMapper.selectUserInfoByUserId(userId);

        List<OauthUserInfoVO> oauthUserInfoVOS = Lists.newArrayListWithCapacity(infoList.size());
        if (!CollectionUtils.isEmpty(infoList)) {
            infoList.forEach(oauthUserInfo -> {
                OauthUserInfoVO oauthUserInfoVO = new OauthUserInfoVO();
                BeanUtils.copyPropertiesIgnoreNull(oauthUserInfo, oauthUserInfoVO);
                oauthUserInfoVOS.add(oauthUserInfoVO);
            });
        }
        return oauthUserInfoVOS;
    }

    /**
     * @param organizationIds, oauthOrganizationVOS
     * @return void
     * @author makaiyu
     * @description 获取机构信息
     * @date 19:25 2019/8/1
     **/
    private void getOrganizationInfo
    (List<Long> organizationIds, List<OauthOrganizationVO> oauthOrganizationVOS) {
        if (!CollectionUtils.isEmpty(organizationIds)) {
            // 获取分组信息
            List<OauthOrganization> oauthOrganizations = getOrganizationByIds(organizationIds);

            if (!CollectionUtils.isEmpty(oauthOrganizations)) {
                oauthOrganizations.forEach(oauthOrganization -> {
                    OauthOrganizationVO oauthOrganizationVO = new OauthOrganizationVO();
                    BeanUtils.copyPropertiesIgnoreNull(oauthOrganization, oauthOrganizationVO);
                    oauthOrganizationVO.setOrganizationId(oauthOrganization.getId());
                    oauthOrganizationVO.setManagerUserId(oauthOrganization.getManagerUserId());
                    oauthOrganizationVOS.add(oauthOrganizationVO);
                });
            }
        }
    }

    /**
     * @param roleIds, oauthRoleVOS
     * @return void
     * @author makaiyu
     * @description 获取用户角色信息
     * @date 19:24 2019/8/1
     **/
    private void getRoleInfo(List<Long> roleIds, List<OauthRoleVO> oauthRoleVOS) {
        if (!CollectionUtils.isEmpty(roleIds)) {
            LambdaQueryWrapper<OauthRole> roleWrapper = new QueryWrapper<OauthRole>().lambda()
                    .in(OauthRole::getId, roleIds);
            List<OauthRole> roles = MapperUtils.list(oauthRoleMapper, roleWrapper);

            if (!CollectionUtils.isEmpty(roles)) {
                roles.forEach(oauthRole -> {
                    OauthRoleVO oauthRoleVO = new OauthRoleVO();
                    BeanUtils.copyPropertiesIgnoreNull(oauthRole, oauthRoleVO);
                    oauthRoleVOS.add(oauthRoleVO);
                });
            }
        }
    }

    /**
     * @param user, groupIds, oauthGroupVOS
     * @return void
     * @author makaiyu
     * @description 获取用户分组信息
     * @date 19:24 2019/8/1
     **/
    private void getGroupInfo(OauthUser user, List<Long> groupIds, List<OauthGroupVO> oauthGroupVOS) {
        if (!CollectionUtils.isEmpty(groupIds)) {
            // 获取分组信息
            List<OauthGroup> groups = getGroupsByIds(groupIds);

            if (!CollectionUtils.isEmpty(groups)) {
                groups.forEach(group -> {
                    OauthGroupVO groupVO = new OauthGroupVO();
                    BeanUtils.copyPropertiesIgnoreNull(group, groupVO);
                    groupVO.setGroupId(group.getId());
                    groupVO.setManagerName(user.getUsername());
                    groupVO.setManagerMail(user.getEmail());
                    groupVO.setManagerPhone(user.getPhoneNumber());
                    oauthGroupVOS.add(groupVO);
                });
            }
        }
    }


}
