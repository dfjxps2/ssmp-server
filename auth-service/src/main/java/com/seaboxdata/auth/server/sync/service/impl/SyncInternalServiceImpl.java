package com.seaboxdata.auth.server.sync.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.dto.OauthUserNamePageDTO;
import com.seaboxdata.auth.api.vo.*;
import com.seaboxdata.auth.server.dao.*;
import com.seaboxdata.auth.server.model.*;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.auth.server.sync.service.ISyncInternalService;
import com.seaboxdata.auth.server.utils.NumberFormatUtils;
import com.seaboxdata.commons.mybatis.MapperUtils;
import com.seaboxdata.commons.query.PaginationResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author ：long
 * @date ：Created in 2020/2/27 下午5:30
 * @description：
 */

@Service
@Slf4j
public class SyncInternalServiceImpl implements ISyncInternalService {


    private OauthUserService oauthUserService;

    private OauthOrganizationMapper oauthOrganizationMapper;

    private OauthUserOrganizationMapper oauthUserOrganizationMapper;

    private OauthUserMapper oauthUserMapper;

    private OauthUserGroupMapper oauthUserGroupMapper;


    private OauthUserRoleMapper oauthUserRoleMapper;


    private OauthUserInfoMapper oauthUserInfoMapper;


    private OauthRoleMapper oauthRoleMapper;


    @Override
    public PaginationResult<OauthUserVO> getAllUser(OauthUserNamePageDTO pageDTO) {
        List<Long> groupUserIds = Lists.newArrayList();
        List<Long> organizationUserIds = Lists.newArrayList();

        log.info("selectAllUser -> param is : {}", pageDTO);

        if (Objects.nonNull(pageDTO) && Objects.nonNull(pageDTO.getGroupId())) {
            // 判断分组Id是否为空
            LambdaQueryWrapper<OauthUserGroup> groupWrapper = new QueryWrapper<OauthUserGroup>().lambda();
            groupWrapper.eq(OauthUserGroup::getGroupId, pageDTO.getGroupId());
            groupUserIds = MapperUtils.list(oauthUserGroupMapper, groupWrapper)
                    .stream().map(OauthUserGroup::getUserId).collect(toList());
        }

        if (Objects.nonNull(pageDTO) && Objects.nonNull(pageDTO.getOrganizationId())) {
            organizationUserIds = getOrganizationUserIds(pageDTO);
        }

        // 1. 根据租户ID 获取全部用户
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda();


        List<Long> allUserIds = Lists.newArrayList();
        allUserIds.addAll(groupUserIds);
        allUserIds.addAll(organizationUserIds);

        if (allUserIds.size() == NumberUtils.INTEGER_ZERO
                && (Objects.nonNull(pageDTO.getGroupId())
                || Objects.nonNull(pageDTO.getOrganizationId()))) {
            return new PaginationResult<>();
        }

        if (!CollectionUtils.isEmpty(allUserIds)) {
            wrapper.in(OauthUser::getId, allUserIds);
        }

        if (Objects.nonNull(pageDTO) && !StringUtils.isEmpty(pageDTO.getName())) {
            wrapper.like(OauthUser::getName, pageDTO.getName());
        }

        List<OauthUser> oauthUsers = Lists.newArrayList();

        // key: roleId   value: OauthRole
        Map<Long, OauthRole> roleMap = MapperUtils.list(oauthRoleMapper, new QueryWrapper<>())
                .stream().collect(Collectors.toMap(OauthRole::getId, Function.identity()));

        List<OauthUserVO> oauthUserVOS = Lists.newArrayList();

        Page<OauthUser> userPage = null;

        // 如果为分页  则获取全部
        if (Objects.isNull(pageDTO.getOffset()) && Objects.isNull(pageDTO.getLimit())) {
            oauthUsers = MapperUtils.list(oauthUserMapper, wrapper);
            List<OauthUserVO> finalOauthUserVOS = oauthUserVOS;
            oauthUsers.forEach(user -> {
                OauthUserVO oauthUserVO = setOauthUserVO(roleMap, user);
                finalOauthUserVOS.add(oauthUserVO);
            });
            oauthUserVOS = finalOauthUserVOS;
        } else {
            userPage = (Page<OauthUser>) MapperUtils.page(oauthUserMapper,
                    new Page<>(pageDTO.getOffset(), pageDTO.getLimit()), wrapper);
            oauthUserVOS = userPage.getRecords().stream().map(user -> setOauthUserVO(roleMap, user)).collect(toList());
        }

        // 2. 封装Page对象
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

    @Override
    public List<OauthUser> queryAllUser(Long orgId) {
        if (Objects.isNull(orgId)) {
            return oauthUserMapper.selectList(null);
        }
        return oauthUserOrganizationMapper.selectList(Wrappers.lambdaQuery(new OauthUserOrganization())
                .eq(OauthUserOrganization::getOrganizationId, orgId))
                .stream()
                .map(this::getOauthUserByUserId)
                .collect(Collectors.toList());
    }


    private OauthUser getOauthUserByUserId(OauthUserOrganization oauthUserOrganization) {
        return oauthUserMapper.selectOne(Wrappers.lambdaQuery(new OauthUser())
                .eq(OauthUser::getId, oauthUserOrganization.getUserId()));
    }

    @Override
    public List<OauthOrganizationVO> getAllOrganization(OauthOrganizationParamDTO oauthOrganizationParamDTO) {

        LambdaQueryWrapper<OauthOrganization> queryWrapper = new QueryWrapper<OauthOrganization>().lambda();

        if (Objects.nonNull(oauthOrganizationParamDTO.getOrganizationId())) {
            log.info("selectAllOrganization -> OrganizationId : {}", oauthOrganizationParamDTO.getOrganizationId());
            queryWrapper.eq(OauthOrganization::getId, oauthOrganizationParamDTO.getOrganizationId());
        }

        if (!StringUtils.isEmpty(oauthOrganizationParamDTO.getKeyWords())) {
            log.info("selectAllOrganization -> OrganizationName : {}", oauthOrganizationParamDTO.getKeyWords());
            queryWrapper.like(OauthOrganization::getOrganizationName, oauthOrganizationParamDTO.getKeyWords())
                    .or()
                    .eq(OauthOrganization::getOrganizationCode, oauthOrganizationParamDTO.getKeyWords())
                    .or()
                    .like(OauthOrganization::getOrganizationAddress, oauthOrganizationParamDTO.getKeyWords());
        }

        // 获取满足查询条件的机构信息
        List<OauthOrganization> organizations = MapperUtils.list(oauthOrganizationMapper, queryWrapper);

        if (CollectionUtils.isEmpty(organizations)) {
            return Lists.newArrayList();
        }

        // 获取满足查询条件的机构ID
        List<Long> organizationIds = organizations.stream().map(OauthOrganization::getId).collect(Collectors.toList());

        LambdaQueryWrapper<OauthUserOrganization> organizationWrapper = new QueryWrapper<OauthUserOrganization>().lambda()
                .in(OauthUserOrganization::getOrganizationId, organizationIds);

        // 获取所有机构用户关联信息
        List<OauthUserOrganization> userOrganizations = MapperUtils.list(oauthUserOrganizationMapper, organizationWrapper);

        // 定义返回List集合
        List<OauthOrganizationVO> organizationVOS = Lists.newArrayListWithCapacity(organizations.size());

        organizations.forEach(oauthOrganization -> {

            LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                    .eq(OauthUser::getId, oauthOrganization.getManagerUserId());

            OauthUser oauthUser = MapperUtils.getOne(oauthUserMapper, wrapper, true);
            OauthOrganizationVO organizationVO = new OauthOrganizationVO();
            BeanUtils.copyProperties(oauthOrganization, organizationVO);
            organizationVO.setOrganizationId(oauthOrganization.getId());
            organizationVO.setManagerUserId(oauthOrganization.getManagerUserId());
            organizationVO.setOrganizationNumber(NumberFormatUtils.numberFormat(
                    (oauthOrganization.getOrganizationNumber())));
            if (Objects.nonNull(oauthUser)) {
                organizationVO.setManagerMail(oauthUser.getEmail() == null ? "" : oauthUser.getEmail());
                organizationVO.setManagerName(oauthUser.getName() == null ? "" : oauthUser.getName());
                organizationVO.setManagerPhone(oauthUser.getPhoneNumber() == null ? "" : oauthUser.getPhoneNumber());
                organizationVO.setUsername(oauthUser.getUsername() == null ? "" : oauthUser.getUsername());
            }

            int userCount = 0;

            if (!CollectionUtils.isEmpty(userOrganizations)) {
                // 查询该机构下 所有用户Id
                List<Long> userIds = userOrganizations.stream().filter(userOrganization -> userOrganization.getOrganizationId()
                        .equals(oauthOrganization.getId())).map(OauthUserOrganization::getUserId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(userIds)) {
                    userCount = userIds.size();
                    List<OauthUserVO> oauthUserVOS = oauthUserService.selectUserByUserId(userIds);
                    organizationVO.setOauthUsers(oauthUserVOS);
                } else {
                    organizationVO.setOauthUsers(Lists.newArrayList());
                }
            }
            organizationVO.setUserCount(userCount);
            organizationVOS.add(organizationVO);
        });
        return organizationVOS;
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
        com.seaboxdata.auth.api.utils.BeanUtils.copyPropertiesIgnoreNull(user, oauthUserVO);
        oauthUserVO.setUserId(user.getId());

        // 获取该用户下 所有角色Id
        selectUserDetail(user, oauthUserVO, roleMap, oauthRoles, oauthGroupVOS,
                oauthOrganizationVOS, oauthUserInfo);
        return oauthUserVO;
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
                    com.seaboxdata.auth.api.utils.BeanUtils.copyPropertiesIgnoreNull(oauthRole, roleVO);
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
                    com.seaboxdata.auth.api.utils.BeanUtils.copyPropertiesIgnoreNull(group, oauthGroupVO);
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
                com.seaboxdata.auth.api.utils.BeanUtils.copyPropertiesIgnoreNull(userInfo, oauthUserInfoVO);
                oauthUserInfoVO.setContact(userInfo.getContact());
                oauthUserInfoVOs.add(oauthUserInfoVO);
            });
        }
        oauthUserVO.setUserInfoVOS(oauthUserInfoVOs);

        // 获取该用户下 所有的机构信息
        List<OauthOrganization> oauthOrganizations = oauthUserOrganizationMapper
                .selectOrganizationUserId(oauthUser.getId(), oauthUser.getTenantId());

        if (!CollectionUtils.isEmpty(oauthOrganizations)) {
            oauthOrganizations.forEach(organization -> {

                OauthOrganizationVO organizationVO = new OauthOrganizationVO();
                organizationVO.setManagerPhone(oauthUser.getPhoneNumber());
                organizationVO.setManagerMail(oauthUser.getEmail());
                if (Objects.nonNull(organization)) {
                    List<OauthUserVO> oauthUserVOS = oauthUserService.selectUserByUserId(Lists.newArrayList(organization.getManagerUserId()));

                    String managerUserName = "";
                    if (!CollectionUtils.isEmpty(oauthUserVOS)) {
                        managerUserName = oauthUserVOS.get(0).getUsername();
                    }
                    com.seaboxdata.auth.api.utils.BeanUtils.copyPropertiesIgnoreNull(organization, organizationVO);
                    organizationVO.setOrganizationId(organization.getId());
                    organizationVO.setManagerName(managerUserName);
                }
                oauthOrganizationVOS.add(organizationVO);
            });
        }
        oauthUserVO.setOrganizations(oauthOrganizationVOS);
    }

    /**
     * @param pageDTO
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 获取机构及子机构下的用户Id
     * @date 10:12 2019/8/26
     **/
    private List<Long> getOrganizationUserIds(@RequestBody(required = false) OauthUserNamePageDTO pageDTO) {
        // 判断机构Id是否为空
        List<Long> organizationUserIds;
        Long organizationId = pageDTO.getOrganizationId();
        Set<Long> allOrganizationIds = Sets.newHashSet();

        // 获取其下子机构的信息
        Set<Long> oauthOrganizationIds = oauthOrganizationMapper
                .selectOrganizationByParent(organizationId);

        if (!CollectionUtils.isEmpty(oauthOrganizationIds)) {
            Set<Long> childOrganizationIds = oauthOrganizationMapper
                    .selectOrganizationByParents(oauthOrganizationIds);
            oauthOrganizationIds.addAll(childOrganizationIds);
        }

        allOrganizationIds.add(organizationId);
        oauthOrganizationIds.addAll(allOrganizationIds);

        if (!CollectionUtils.isEmpty(oauthOrganizationIds)) {
            LambdaQueryWrapper<OauthOrganization> wrapper = new QueryWrapper<OauthOrganization>().lambda()
                    .in(OauthOrganization::getId, oauthOrganizationIds);
            List<OauthOrganization> list = MapperUtils.list(oauthOrganizationMapper, wrapper);
            if (!CollectionUtils.isEmpty(list)) {
                allOrganizationIds.addAll(list.stream().map(OauthOrganization::getId).collect(toList()));
            }
        }

        LambdaQueryWrapper<OauthUserOrganization> organizationWrapper = new QueryWrapper<OauthUserOrganization>().lambda();
        organizationWrapper.in(OauthUserOrganization::getOrganizationId, allOrganizationIds);
        organizationUserIds = MapperUtils.list(oauthUserOrganizationMapper, organizationWrapper)
                .stream().map(OauthUserOrganization::getUserId).collect(toList());
        return organizationUserIds;
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

    @Autowired
    public SyncInternalServiceImpl(OauthUserService oauthUserService
            , OauthOrganizationMapper oauthOrganizationMapper
            , OauthUserOrganizationMapper oauthUserOrganizationMapper
            , OauthUserMapper oauthUserMapper
            , OauthUserGroupMapper oauthUserGroupMapper
            , OauthUserRoleMapper oauthUserRoleMapper
            , OauthUserInfoMapper oauthUserInfoMapper
            , OauthRoleMapper oauthRoleMapper) {
        this.oauthUserService = oauthUserService;
        this.oauthOrganizationMapper = oauthOrganizationMapper;
        this.oauthUserOrganizationMapper = oauthUserOrganizationMapper;
        this.oauthUserMapper = oauthUserMapper;
        this.oauthUserGroupMapper = oauthUserGroupMapper;
        this.oauthUserRoleMapper = oauthUserRoleMapper;
        this.oauthUserInfoMapper = oauthUserInfoMapper;
        this.oauthRoleMapper = oauthRoleMapper;
    }
}
