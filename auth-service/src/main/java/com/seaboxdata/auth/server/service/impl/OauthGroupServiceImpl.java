package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.dto.OauthGroupDTO;
import com.seaboxdata.auth.api.dto.OauthGroupParamDTO;
import com.seaboxdata.auth.api.dto.OauthUserGroupDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthGroupVO;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.dao.OauthGroupMapper;
import com.seaboxdata.auth.server.dao.OauthUserGroupMapper;
import com.seaboxdata.auth.server.dao.OauthUserMapper;
import com.seaboxdata.auth.server.model.OauthGroup;
import com.seaboxdata.auth.server.model.OauthOrganization;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.model.OauthUserGroup;
import com.seaboxdata.auth.server.service.OauthGroupService;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.auth.server.utils.Redis;
import com.seaboxdata.auth.server.utils.RedisKeyConst;
import com.seaboxdata.auth.server.utils.RedisUtil;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import com.seaboxdata.commons.query.PaginationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户分组服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
@Service
@Slf4j
public class OauthGroupServiceImpl implements OauthGroupService {

    @Autowired
    private OauthUserService oauthUserService;

    @Autowired
    private OauthUserGroupMapper oauthUserGroupMapper;

    @Autowired
    private OauthUserMapper oauthUserMapper;

    @Autowired
    private OauthGroupMapper oauthGroupMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * @param oauthGroupDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 增加分组信息
     * @date 15:11 2019/5/31
     **/
    @Override
    public Boolean saveOauthGroup(@RequestBody OauthGroupDTO oauthGroupDTO) {
        if (Objects.isNull(oauthGroupDTO)) {
            throw new ServiceException("400", "saveOauthGroup param is null");
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        log.info("saveOauthGroup -> oauthGroupDTO : {} ", oauthGroupDTO);

        OauthUser oauthUser = oauthUserService.getByUsername(oauthGroupDTO.getManagerName().trim());

        if (Objects.isNull(oauthUser)) {
            log.info("saveOauthGroup -> oauthUser is null");
            throw new ServiceException("701", "管理者用户名不存在");
        }


        //更新用户联系电话，邮箱
        oauthUser.setEmail(oauthGroupDTO.getManagerMail());
        oauthUser.setPhoneNumber(oauthGroupDTO.getManagerPhone());
        oauthUser.setUpdateTime(LocalDateTime.now());
        MapperUtils.updateById(oauthUserMapper, oauthUser);


        OauthGroup oauthGroup = new OauthGroup();
        BeanUtils.copyProperties(oauthGroupDTO, oauthGroup);
        oauthGroup.setUserId(oauthUser.getId());
        oauthGroup.setTenantId(tenantId);
        try {
            if (Objects.nonNull(oauthGroupDTO.getGroupId())) {
                oauthGroup.setId(oauthGroupDTO.getGroupId());
                oauthGroup.setUpdateTime(LocalDateTime.now());
                MapperUtils.updateById(oauthGroupMapper, oauthGroup);
            } else {
                MapperUtils.save(oauthGroupMapper, oauthGroup);
            }
        } catch (Exception e) {
            log.warn("error : {} ", e.getMessage());
            throw new ServiceException("500", "保存分组失败");
        }

        return true;
    }

    /**
     * @param oauthGroupParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthGroupDTO>
     * @author makaiyu
     * @description 查询全部分组
     * @date 15:19 2019/5/31
     */
    @Override
    public List<OauthGroupVO> selectAllGroup(@RequestBody OauthGroupParamDTO oauthGroupParamDTO) {

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        LambdaQueryWrapper<OauthGroup> queryWrapper = new QueryWrapper<OauthGroup>().lambda()
                .eq(OauthGroup::getTenantId, tenantId);

        if (Objects.nonNull(oauthGroupParamDTO) && !StringUtils.isEmpty(oauthGroupParamDTO.getKeyWords())) {
            queryWrapper.like(OauthGroup::getGroupName, oauthGroupParamDTO.getKeyWords())
                    .or()
                    .like(OauthGroup::getGroupDesc, oauthGroupParamDTO.getKeyWords());
        }

        if (Objects.nonNull(oauthGroupParamDTO) && Objects.nonNull(oauthGroupParamDTO.getGroupId())) {
            queryWrapper.eq(OauthGroup::getId, oauthGroupParamDTO.getGroupId());
        }

        // 查询全部分组
        List<OauthGroup> oauthGroups = MapperUtils.list(oauthGroupMapper, queryWrapper);

        if (CollectionUtils.isEmpty(oauthGroups)) {
            log.info("selectAllGroup -> oauthGroups is null");
            return Lists.newArrayList();
        }

        // 获取全部负责人信息 组成map
        List<Long> managerUserIds = oauthGroups.stream().map(OauthGroup::getUserId).collect(Collectors.toList());
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getTenantId, tenantId).in(OauthUser::getId, managerUserIds);

        Map<Long,OauthUser> managersMap = MapperUtils.list(oauthUserMapper, wrapper).stream().collect(Collectors.toMap(OauthUser::getId, Function.identity()));

        List<OauthGroupVO> oauthGroupVOS = Lists.newArrayListWithCapacity(oauthGroups.size());

        if (!CollectionUtils.isEmpty(oauthGroups)) {
            oauthGroups.forEach(oauthGroup -> {

                OauthGroupVO oauthGroupVO = new OauthGroupVO();
                BeanUtils.copyProperties(oauthGroup, oauthGroupVO);
                oauthGroupVO.setGroupId(oauthGroup.getId());

                // 设置负责人信息
                OauthUser oauthUser = managersMap.get(oauthGroup.getUserId());
                if (Objects.nonNull(oauthUser)) {
                    oauthGroupVO.setManagerName(oauthUser.getName() == null ? "" : oauthUser.getName());
                    oauthGroupVO.setUsername(oauthUser.getUsername() == null ? "" : oauthUser.getUsername());
                    oauthGroupVO.setManagerMail(oauthUser.getEmail() == null ? "" : oauthUser.getEmail());
                    oauthGroupVO.setManagerPhone(oauthUser.getPhoneNumber() == null ? "" : oauthUser.getPhoneNumber());
                }
                oauthGroupVOS.add(oauthGroupVO);
            });
        }

        return oauthGroupVOS;
    }

    /**
     * @param userId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据用户Id 获取分组Id
     * @date 10:52 2020-04-02
     **/
    @Override
    public List<Long> selectGroupIdByUserId(Long userId) {

        if (Objects.isNull(userId)) {
            return Lists.newArrayList();
        }

        LambdaQueryWrapper<OauthUserGroup> wrapper = new QueryWrapper<OauthUserGroup>().lambda()
                .eq(OauthUserGroup::getUserId, userId);

        List<OauthUserGroup> groups = MapperUtils.list(oauthUserGroupMapper, wrapper);

        if (!CollectionUtils.isEmpty(groups)) {
            return groups.stream().map(OauthUserGroup::getGroupId).collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }

    /**
     * @param groupIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据Id 删除分组
     * @date 15:26 2019/5/31
     **/
    @Override
    public Boolean deleteOauthGroup(@RequestBody List<Long> groupIds) {

        if (CollectionUtils.isEmpty(groupIds)) {
            throw new ServiceException("400", "deleteOauthGroup param is null");
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        log.info("deleteOauthGroup -> groupIds : {} ", groupIds);

        try {
            // 1.删除用户关联分组信息
            LambdaQueryWrapper<OauthUserGroup> wrapper = new QueryWrapper<OauthUserGroup>().lambda()
                    .in(OauthUserGroup::getGroupId, groupIds)
                    .eq(OauthUserGroup::getTenantId, tenantId);

            MapperUtils.remove(oauthUserGroupMapper, wrapper);

            // 2. 删除分组信息
            MapperUtils.removeByIds(oauthGroupMapper, groupIds);

        } catch (Exception e) {
            throw new ServiceException("500", "删除分组失败");
        }

        return true;
    }

    /**
     * @param oauthGroupDTO
     * @return com.seaboxdata.auth.api.vo.OauthGroupVO
     * @author makaiyu
     * @description 根据分组Id  获取组内成员
     * @date 15:39 2019/6/3
     **/
    @Override
    public PaginationResult<OauthUserVO> selectUserByGroupId(@RequestBody OauthGroupDTO oauthGroupDTO) {
        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        if (Objects.isNull(oauthGroupDTO) || Objects.isNull(oauthGroupDTO.getGroupId())) {
            log.info("selectUserByGroupId -> oauthGroupDTO || groupId is null");
            PaginationResult<OauthUserVO> paginationResult = new PaginationResult<>();
            paginationResult.setData(Lists.newArrayList());
            return paginationResult;
        }

        log.info("selectUserByGroupId -> oauthGroupDTO : {} ", oauthGroupDTO);

        // 1. 根据groupId 获取 userId
        LambdaQueryWrapper<OauthUserGroup> wrapper = new QueryWrapper<OauthUserGroup>().lambda()
                .eq(OauthUserGroup::getGroupId, oauthGroupDTO.getGroupId())
                .eq(OauthUserGroup::getTenantId, tenantId);

        List<OauthUserGroup> userGroups = Lists.newArrayList();

        if (Objects.isNull(oauthGroupDTO.getLimit()) || Objects.isNull(oauthGroupDTO.getOffset())) {
            userGroups = MapperUtils.list(oauthUserGroupMapper, wrapper);
        } else {
            Page<OauthUserGroup> groupPage = (Page<OauthUserGroup>) MapperUtils.page(oauthUserGroupMapper,
                    new Page<>(oauthGroupDTO.getOffset(), oauthGroupDTO.getLimit()), wrapper);
            userGroups = groupPage.getRecords();
        }

        List<Long> userIds = userGroups
                .stream().map(OauthUserGroup::getUserId).collect(Collectors.toList());

        // 2. 根据userIds 获取用户信息
        if (CollectionUtils.isEmpty(userIds)) {
            log.info("selectUserByGroupId -> userId 为空 ");
            PaginationResult<OauthUserVO> paginationResult = new PaginationResult<>();
            paginationResult.setData(Lists.newArrayList());
            return paginationResult;
        }

        LambdaQueryWrapper<OauthUser> queryWrapper = new QueryWrapper<OauthUser>().lambda()
                .in(OauthUser::getId, userIds);

        List<OauthUser> oauthUsers = MapperUtils.list(oauthUserMapper, queryWrapper);

        List<OauthUserVO> oauthUserVOS = Lists.newArrayListWithCapacity(oauthUsers.size());

        // 3. 装入返回对象
        if (!CollectionUtils.isEmpty(oauthUsers)) {
            oauthUsers.forEach(oauthUser -> {
                OauthUserVO oauthUserVO = new OauthUserVO();
                BeanUtils.copyProperties(oauthUser, oauthUserVO);
                oauthUserVO.setUserId(oauthUser.getId());
                oauthUserVOS.add(oauthUserVO);
            });
        }

        PaginationResult<OauthUserVO> paginationResult = new PaginationResult<>();
        paginationResult.setData(oauthUserVOS);
        paginationResult.setTotal(oauthUserVOS.size());
        paginationResult.setOffset(oauthGroupDTO.getOffset());
        paginationResult.setLimit(oauthGroupDTO.getLimit());

        return paginationResult;
    }


    /**
     * @param oauthUserGroupDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个分组 -> 多个用户
     * @date 14:29 2020-04-26
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateUserGroup(@RequestBody OauthUserGroupDTO oauthUserGroupDTO) {
        if (Objects.isNull(oauthUserGroupDTO) || Objects.isNull(oauthUserGroupDTO.getGroupId())) {
            log.info("saveOrUpdateUserRole -> param is null!");
            return false;
        }

        try {
            Long groupId = oauthUserGroupDTO.getGroupId();
            List<Long> userIds = Lists.newArrayList();
            if (!CollectionUtils.isEmpty(oauthUserGroupDTO.getAddUserId())) {
                userIds.addAll(oauthUserGroupDTO.getAddUserId());
            }

            if (!CollectionUtils.isEmpty(oauthUserGroupDTO.getDeleteUserId())) {
                userIds.addAll(oauthUserGroupDTO.getDeleteUserId());
            }

            if (CollectionUtils.isEmpty(userIds)) {
                log.info("saveOrUpdateUserGroup -> userIds is null!");
                return true;
            }

            // 1. 删除原有对应关系
            LambdaQueryWrapper<OauthUserGroup> wrapper = new QueryWrapper<OauthUserGroup>().lambda()
                    .in(OauthUserGroup::getUserId, userIds)
                    .eq(OauthUserGroup::getGroupId, groupId);

            MapperUtils.remove(oauthUserGroupMapper, wrapper);

            // 2. 增加新的对应关系
            Long tenantId = UserUtils.getUserDetails().getTenantId();
            if (!CollectionUtils.isEmpty(oauthUserGroupDTO.getAddUserId())) {
                oauthUserGroupDTO.getAddUserId().forEach(userId -> {
                    OauthUserGroup oauthUserRole = new OauthUserGroup();
                    oauthUserRole.setGroupId(groupId)
                            .setTenantId(tenantId)
                            .setUserId(userId);
                    MapperUtils.save(oauthUserGroupMapper, oauthUserRole);
                });
            }
        } catch (Exception e) {
            log.error("saveOrUpdateUserRole -> ", e);
            throw new ServiceException("500", "修改分组匹配用户失败");
        }

        return true;
    }
}
