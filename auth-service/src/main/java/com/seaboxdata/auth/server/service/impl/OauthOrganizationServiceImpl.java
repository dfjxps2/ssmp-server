package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seaboxdata.auth.api.dto.OauthOrganizationDTO;
import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.api.vo.OauthOrganizationRedisVo;
import com.seaboxdata.auth.api.vo.OauthOrganizationVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.dao.OauthOrganizationMapper;
import com.seaboxdata.auth.server.dao.OauthUserMapper;
import com.seaboxdata.auth.server.dao.OauthUserOrganizationMapper;
import com.seaboxdata.auth.server.model.OauthOrganization;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.model.OauthUserOrganization;
import com.seaboxdata.auth.server.mq.AuthProducter;
import com.seaboxdata.auth.server.service.OauthOrganizationService;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.auth.server.utils.NumberFormatUtils;
import com.seaboxdata.auth.server.utils.RedisUtil;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import com.seaboxdata.jxpm.api.controller.IPmProductController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
@Service
@Slf4j
public class OauthOrganizationServiceImpl implements OauthOrganizationService {

    @Autowired
    private OauthUserService oauthUserService;

    @Autowired
    private OauthUserOrganizationMapper oauthUserOrganizationMapper;

    @Autowired
    private OauthOrganizationMapper oauthOrganizationMapper;

    @Autowired
    private OauthUserMapper oauthUserMapper;

    @Autowired
    private AuthProducter authProducter;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IPmProductController pmProductController;

    /**
     * @param oauthOrganizationDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存机构信息
     * @date 10:48 2019/5/31
     **/
    @Override
    public Boolean saveOauthOrganization(@RequestBody OauthOrganizationDTO oauthOrganizationDTO,
                                         Long userId, Long tenantId) {

        if (Objects.isNull(oauthOrganizationDTO)) {
            throw new ServiceException("400", "saveOauthOrganization param is null");
        }

        // 获取负责人信息
        OauthUser user = oauthUserService.getByUsername(oauthOrganizationDTO.getManagerName().trim());

        if (Objects.isNull(user)) {
            log.info("saveOauthOrganization -> user is null");
            throw new ServiceException("701", "责任人信息不存在");
        }

        OauthOrganization oauthOrganization = new OauthOrganization();
        oauthOrganization.setManagerUserId(user.getId());
        BeanUtils.copyProperties(oauthOrganizationDTO, oauthOrganization);
        oauthOrganization.setTenantId(tenantId);

        OauthOrganizationRedisVo vo = new OauthOrganizationRedisVo();

        if (Objects.isNull(oauthOrganizationDTO.getParentId())) {
            oauthOrganization.setParentId(NumberUtils.LONG_ZERO);
        }

        try {
            Map<String, Object> map = Maps.newHashMap();
            if (Objects.nonNull(oauthOrganizationDTO.getOrganizationId())) {
                oauthOrganization.setId(oauthOrganizationDTO.getOrganizationId());
                oauthOrganization.setUpdateTime(LocalDateTime.now());
                MapperUtils.updateById(oauthOrganizationMapper, oauthOrganization);
                BeanUtils.copyProperties(oauthOrganization, oauthOrganizationDTO);
                map.put(oauthOrganization.getId().toString(), oauthOrganizationDTO);
                try {
                    // 发送消息
                    authProducter.updateOrganizationTag(oauthOrganization.getId(),
                            oauthOrganization.getOrganizationName(), userId, tenantId, map);
                } catch (Exception e) {
                    log.error("机构添加修改 mq消息发送失败 ！");
                }

                BeanUtils.copyProperties(oauthOrganization, vo);
                redisUtil.hset("organization:" + oauthOrganization.getTenantId(), oauthOrganization.getId().toString(), vo);
            } else {
                /* 机构验重 */
                this.checkOrganizationRepeat(oauthOrganizationDTO);
                if (!redisUtil.exists("organization:" + oauthOrganization.getTenantId())) {
                    QueryWrapper<OauthOrganization> query = new QueryWrapper();
                    query.lambda().select()
                            .eq(OauthOrganization::getTenantId, UserUtils.getUserDetails().getTenantId());
                    List<OauthOrganization> oauthOrganizations = oauthOrganizationMapper.selectList(query);
                    Map<String, Object> collect = oauthOrganizations.stream().map(oauthOrganization1 -> {
                        OauthOrganizationRedisVo redisVo = new OauthOrganizationRedisVo();
                        BeanUtils.copyProperties(oauthOrganization1, redisVo);
                        return redisVo;
                    }).collect(Collectors.toMap(o -> String.valueOf(o.getId()), o -> o));
                    redisUtil.hmset("organization:" + UserUtils.getUserDetails().getTenantId(), collect);
                }


//                if (!redisUtil.exists("organization:" + oauthOrganization.getTenantId())) {
//                    QueryWrapper<OauthOrganization> query = new QueryWrapper();
//                    query.lambda().select()
//                            .eq(OauthOrganization::getTenantId, UserUtils.getUserDetails().getTenantId());
//                    List<OauthOrganization> oauthOrganizations = oauthOrganizationMapper.selectList(query);
//                    Map<String, Object> collect = oauthOrganizations.stream().map(oauthOrganization1 -> {
//                        OauthOrganizationRedisVo redisVo = new OauthOrganizationRedisVo();
//                        BeanUtils.copyProperties(oauthOrganization1, redisVo);
//                        return redisVo;
//                    }).collect(Collectors.toMap(o -> String.valueOf(o.getId()), o -> o));
//                    redisUtil.hmset("organization:" + UserUtils.getUserDetails().getTenantId(), collect);
//                }


                MapperUtils.save(oauthOrganizationMapper, oauthOrganization);
                oauthOrganizationDTO.setOrganizationId(oauthOrganization.getId());
                BeanUtils.copyProperties(oauthOrganization, oauthOrganizationDTO);
                map.put(oauthOrganization.getId().toString(), oauthOrganizationDTO);
                try {
                    // 发送消息
                    authProducter.saveOrganizationTag(oauthOrganization.getId(),
                            oauthOrganization.getOrganizationName(), userId, tenantId, map);
                } catch (Exception e) {
                    log.error("机构添加修改 mq消息发送失败 ！");
                }

                BeanUtils.copyProperties(oauthOrganization, vo);
                redisUtil.hset("organization:" + oauthOrganization.getTenantId(), oauthOrganization.getId().toString(), vo);
            }
            return true;
        }catch(ServiceException e1){
            System.out.println(e1.getCode());
            System.out.println(e1.getMessage());
          throw new ServiceException(e1.getCode(),e1.getMessage());
        } catch (Exception e) {
            throw new ServiceException("500", "保存机构失败");
        }
    }

    /**
     * 验证机构是否重复
     */
    private void checkOrganizationRepeat(OauthOrganizationDTO dto)
    {
        LambdaQueryWrapper<OauthOrganization> wrapper = new QueryWrapper<OauthOrganization>().lambda()
                .eq(OauthOrganization::getOrganizationName,dto.getOrganizationName())
                .eq(OauthOrganization::getOrganizationCode,dto.getOrganizationCode())
                .eq(OauthOrganization::getTenantId,UserUtils.getUserDetails().getTenantId());
        OauthOrganization row = MapperUtils.getOne(oauthOrganizationMapper,wrapper,false);
        if(!Objects.isNull(row))
        {
            throw new ServiceException("400",dto.getOrganizationName() + "已经存在，勿重复添加");
        }
    }

    /**
     * @param userId
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据用户Id获取机构信息
     * @date 14:04 2020-01-14
     */
    @Override
    public List<OauthOrganizationVO> selectOrganizationByUserId(Long userId) {

        LambdaQueryWrapper<OauthUserOrganization> organizationWrapper = new QueryWrapper<OauthUserOrganization>().lambda()
                .in(OauthUserOrganization::getUserId, userId);

        // 获取所有机构用户关联信息
        List<OauthUserOrganization> userOrganizations = MapperUtils.list(oauthUserOrganizationMapper, organizationWrapper);

        List<OauthOrganizationVO> result = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(userOrganizations)) {
            Set<Long> organizationIds = userOrganizations.stream()
                    .map(OauthUserOrganization::getOrganizationId).collect(Collectors.toSet());

            Set<Long> resultOrganizationIds = Sets.newHashSet();

            resultOrganizationIds.addAll(organizationIds);
            organizationIds.forEach(organizationId -> {
                // 1. 寻找其子级Id
                selectChildOrganizationById(resultOrganizationIds, organizationId);

                // 2.寻找父级Id
                selectParentOrganizationById(resultOrganizationIds, organizationId);
            });

            if (!CollectionUtils.isEmpty(resultOrganizationIds)) {
                LambdaQueryWrapper<OauthOrganization> wrapper = new QueryWrapper<OauthOrganization>().lambda()
                        .in(OauthOrganization::getId, resultOrganizationIds);

                List<OauthOrganization> organizationList = MapperUtils.list(oauthOrganizationMapper, wrapper);

                organizationList.forEach(oauthOrganization -> {
                    OauthOrganizationVO oauthOrganizationVO = new OauthOrganizationVO();
                    BeanUtils.copyProperties(oauthOrganization, oauthOrganizationVO);
                    oauthOrganizationVO.setOrganizationId(oauthOrganization.getId());
                    result.add(oauthOrganizationVO);
                });
            }
        }

        return result;
    }

    /**
     * @param organizationName
     * @return com.seaboxdata.auth.api.vo.OauthOrganizationVO
     * @author makaiyu
     * @description 根据OrganizationName 获取机构信息
     * @date 16:53 2020-03-05
     **/
    @Override
    public OauthOrganizationVO selectOrganizationByName(String organizationName) {

        LambdaQueryWrapper<OauthOrganization> wrapper = new QueryWrapper<OauthOrganization>().lambda()
                .eq(OauthOrganization::getOrganizationName, organizationName)
                .eq(OauthOrganization::getTenantId, UserUtils.getUserDetails().getTenantId());

        List<OauthOrganization> organizations = MapperUtils.list(oauthOrganizationMapper, wrapper);

        OauthOrganization oauthOrganization = new OauthOrganization();

        if (!CollectionUtils.isEmpty(organizations)) {
            oauthOrganization = organizations.get(0);
        }

        OauthOrganizationVO oauthOrganizationVO = new OauthOrganizationVO();

        if (Objects.nonNull(oauthOrganization)) {
            BeanUtils.copyProperties(oauthOrganization, oauthOrganizationVO);
            oauthOrganizationVO.setOrganizationId(oauthOrganization.getId());
        }

        return oauthOrganizationVO;
    }

    /**
     * 根据租户查询所有机构名
     *
     * @Author LiuZhanXi
     * @Date 2020/3/25 19:30
     **/
    @Override
    public List<OauthOrganizationRedisVo> selectAllOrganizationName() {

        Map<Object, Object> hmget = redisUtil.hmget("organization:" + UserUtils.getUserDetails().getTenantId().toString());
        if (hmget.isEmpty()) {
            QueryWrapper<OauthOrganization> query = new QueryWrapper<>();
            query.lambda().select()
                    .eq(OauthOrganization::getTenantId, UserUtils.getUserDetails().getTenantId())
                    .orderByAsc(OauthOrganization::getUpdateTime);
            List<OauthOrganization> oauthOrganizations = oauthOrganizationMapper.selectList(query);
            if (!oauthOrganizations.isEmpty()) {
                Map<String, Object> redisVoMap = oauthOrganizations.stream().map(oauthOrganization -> {
                    OauthOrganizationRedisVo vo = new OauthOrganizationRedisVo();
                    BeanUtils.copyProperties(oauthOrganization, vo);
                    return vo;
                }).collect(Collectors.toMap(o -> String.valueOf(o.getId()), o -> o));

                redisUtil.hmset("organization:" + UserUtils.getUserDetails().getTenantId(), redisVoMap);
                return redisUtil.hmget("organization:" + UserUtils.getUserDetails().getTenantId().toString())
                        .values().stream()
                        .map(o -> (OauthOrganizationRedisVo) o)
                        .collect(Collectors.toList());

            }
        }
        return hmget.values().stream().map(o -> (OauthOrganizationRedisVo) o).collect(Collectors.toList());
    }


    /**
     * @param result, organizationId
     * @return java.util.Set<java.lang.Long>
     * @author makaiyu
     * @description 寻找子级机构Id
     * @date 14:06 2020-01-14
     **/
    private Set<Long> selectChildOrganizationById(Set<Long> result, Long organizationId) {

        LambdaQueryWrapper<OauthOrganization> eq = new QueryWrapper<OauthOrganization>().lambda()
                .eq(OauthOrganization::getParentId, organizationId);

        List<OauthOrganization> list = MapperUtils.list(oauthOrganizationMapper, eq);

        if (!CollectionUtils.isEmpty(list)) {
            result.addAll(list.stream().map(OauthOrganization::getId).collect(Collectors.toSet()));
            list.forEach(organization -> {
                selectChildOrganizationById(result, organization.getId());
            });
        }
        return result;
    }

    /**
     * @param result, organizationId
     * @return java.util.Set<java.lang.Long>
     * @author makaiyu
     * @description 寻找父级机构Id
     * @date 14:06 2020-01-14
     **/
    private Set<Long> selectParentOrganizationById(Set<Long> result, Long organizationId) {

        LambdaQueryWrapper<OauthOrganization> wrapper = new QueryWrapper<OauthOrganization>().lambda()
                .eq(OauthOrganization::getId, organizationId);

        OauthOrganization oauthOrganization = MapperUtils.getOne(oauthOrganizationMapper, wrapper, true);

        if (NumberUtils.LONG_ZERO.equals(oauthOrganization.getParentId())) {
            return result;
        }

        LambdaQueryWrapper<OauthOrganization> eq = new QueryWrapper<OauthOrganization>().lambda()
                .eq(OauthOrganization::getId, oauthOrganization.getParentId());

        List<OauthOrganization> list = MapperUtils.list(oauthOrganizationMapper, eq);

        if (!CollectionUtils.isEmpty(list)) {
            result.addAll(list.stream().map(OauthOrganization::getId).collect(Collectors.toSet()));
            list.forEach(organization -> {
                selectParentOrganizationById(result, organization.getId());
            });
        }

        return result;
    }


    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 获取全部机构及子机构信息
     * @date 13:00 2019/5/31
     **/
    @Override
    public List<OauthOrganizationVO> selectAllOrganization(@RequestBody(required = false)
                                                                   OauthOrganizationParamDTO oauthOrganizationParamDTO) {

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

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
        queryWrapper.eq(OauthOrganization::getTenantId, tenantId);

        // 获取满足查询条件的机构信息
        List<OauthOrganization> organizations = MapperUtils.list(oauthOrganizationMapper, queryWrapper);

        if (CollectionUtils.isEmpty(organizations)) {
            return Lists.newArrayList();
        }

        // 获取满足查询条件的机构ID
        List<Long> organizationIds = organizations.stream().map(OauthOrganization::getId).collect(Collectors.toList());

        LambdaQueryWrapper<OauthUserOrganization> organizationWrapper = new QueryWrapper<OauthUserOrganization>().lambda()
                .in(OauthUserOrganization::getOrganizationId, organizationIds);

        // 获取机构下用户关联信息
        List<OauthUserOrganization> userOrganizations = MapperUtils.list(oauthUserOrganizationMapper, organizationWrapper);

        // 定义返回List集合
        List<OauthOrganizationVO> organizationVOS = Lists.newArrayListWithCapacity(organizations.size());

        // 获取全部负责人信息
        List<Long> managerUserIds = organizations.stream().map(OauthOrganization::getManagerUserId).collect(Collectors.toList());
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getTenantId, tenantId).in(OauthUser::getId, managerUserIds);

        Map<Long,OauthUser> managersMap = MapperUtils.list(oauthUserMapper, wrapper).stream().collect(Collectors.toMap(OauthUser::getId, Function.identity()));

        // 拼装机构信息返回
        organizations.forEach(oauthOrganization -> {
            OauthOrganizationVO organizationVO = new OauthOrganizationVO();

            BeanUtils.copyProperties(oauthOrganization, organizationVO);
            organizationVO.setOrganizationId(oauthOrganization.getId());
            organizationVO.setManagerUserId(oauthOrganization.getManagerUserId());
            organizationVO.setOrganizationNumber(NumberFormatUtils.numberFormat((oauthOrganization.getOrganizationNumber())));

            OauthUser manager = managersMap.get(oauthOrganization.getManagerUserId());
            if (Objects.nonNull(manager)) {
                organizationVO.setManagerMail(manager.getEmail() == null ? "" : manager.getEmail());
                organizationVO.setManagerName(manager.getName() == null ? "" : manager.getName());
                organizationVO.setManagerPhone(manager.getPhoneNumber() == null ? "" : manager.getPhoneNumber());
                organizationVO.setUsername(manager.getUsername() == null ? "" : manager.getUsername());
            }

            int userCount = 0;
            if (!CollectionUtils.isEmpty(userOrganizations)) {
                // 查询该机构下 所有用户Id
                List<Long> userIds = userOrganizations.stream().filter(userOrganization -> userOrganization.getOrganizationId()
                        .equals(oauthOrganization.getId())).map(OauthUserOrganization::getUserId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(userIds)) {
                    userCount = userIds.size();
                }
            }
            organizationVO.setUserCount(userCount);
            organizationVOS.add(organizationVO);
        });
        return organizationVOS;
    }



    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 获取全部机构及机构下用户信息
     * @date 13:00 2019/5/31
     **/
    @Override
    public List<OauthOrganizationVO> selectAllOrganizationAndUesrInfo(@RequestBody(required = false)
                                                                   OauthOrganizationParamDTO oauthOrganizationParamDTO) {

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        LambdaQueryWrapper<OauthOrganization> queryWrapper = new QueryWrapper<OauthOrganization>().lambda();

        if (Objects.nonNull(oauthOrganizationParamDTO.getOrganizationId())) {
            log.info("selectAllOrganizationAndUesrInfo -> OrganizationId : {}", oauthOrganizationParamDTO.getOrganizationId());
            queryWrapper.eq(OauthOrganization::getId, oauthOrganizationParamDTO.getOrganizationId());
        }

        if (!StringUtils.isEmpty(oauthOrganizationParamDTO.getKeyWords())) {
            log.info("selectAllOrganizationAndUesrInfo -> OrganizationName : {}", oauthOrganizationParamDTO.getKeyWords());
            queryWrapper.like(OauthOrganization::getOrganizationName, oauthOrganizationParamDTO.getKeyWords())
                    .or()
                    .eq(OauthOrganization::getOrganizationCode, oauthOrganizationParamDTO.getKeyWords())
                    .or()
                    .like(OauthOrganization::getOrganizationAddress, oauthOrganizationParamDTO.getKeyWords());
        }
        queryWrapper.eq(OauthOrganization::getTenantId, tenantId);

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

        // 获取全部负责人信息
        List<Long> managerUserIds = organizations.stream().map(OauthOrganization::getManagerUserId).collect(Collectors.toList());
        LambdaQueryWrapper<OauthUser> wrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getTenantId, tenantId).in(OauthUser::getId, managerUserIds);

        Map<Long,OauthUser> managersMap = MapperUtils.list(oauthUserMapper, wrapper).stream().collect(Collectors.toMap(OauthUser::getId, Function.identity()));

        // 拼装机构信息返回
        organizations.forEach(oauthOrganization -> {
            OauthOrganizationVO organizationVO = new OauthOrganizationVO();

            BeanUtils.copyProperties(oauthOrganization, organizationVO);
            organizationVO.setOrganizationId(oauthOrganization.getId());
            organizationVO.setManagerUserId(oauthOrganization.getManagerUserId());
            organizationVO.setOrganizationNumber(NumberFormatUtils.numberFormat((oauthOrganization.getOrganizationNumber())));

            OauthUser manager = managersMap.get(oauthOrganization.getManagerUserId());
            if (Objects.nonNull(manager)) {
                organizationVO.setManagerMail(manager.getEmail() == null ? "" : manager.getEmail());
                organizationVO.setManagerName(manager.getName() == null ? "" : manager.getName());
                organizationVO.setManagerPhone(manager.getPhoneNumber() == null ? "" : manager.getPhoneNumber());
                organizationVO.setUsername(manager.getUsername() == null ? "" : manager.getUsername());
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
     * @param organizationIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除机构
     * @date 14:11 2019/5/31
     **/
    @Override
    public Boolean deleteOauthOrganization(@RequestBody List<Long> organizationIds) {

        if (CollectionUtils.isEmpty(organizationIds)) {
            throw new ServiceException("400", "删除机构参数为空");
        }

        Boolean flag = pmProductController.checkProductOrganizationByIds(organizationIds);
        if (flag) {
            throw new ServiceException("505", "所选机构已被其他系统引用，请检查后删除！");
        }

        log.info("deleteOauthOrganization -> organizationIds : {}", organizationIds);

        try {
            // 1. 修改用户关联机构信息
            LambdaUpdateWrapper<OauthUserOrganization> updateWrapper = new UpdateWrapper<OauthUserOrganization>().lambda()
                    .in(OauthUserOrganization::getOrganizationId, organizationIds)
                    .set(OauthUserOrganization::getOrganizationId, NumberUtils.LONG_ZERO);

            MapperUtils.update(oauthUserOrganizationMapper, null, updateWrapper);

            // 2. 删除机构信息
            MapperUtils.removeByIds(oauthOrganizationMapper, organizationIds);
//            for (Long organizationId : organizationIds) {
//
//                Map<String, Object> map = Maps.newHashMap();
//
//                redisUtil.deleteValueByKey("organization:" + UserUtils.getUserDetails().getTenantId(), organizationId.toString());
//
//                QueryWrapper<OauthOrganization> queryWrapper = new QueryWrapper<>();
//                queryWrapper.lambda().select()
//                        .eq(OauthOrganization::getId, organizationId)
//                        .eq(OauthOrganization::getTenantId, UserUtils.getUserDetails().getTenantId());
//                OauthOrganization oauthOrganization = oauthOrganizationMapper.selectOne(queryWrapper);
//                if (oauthOrganization != null) {
//                    MapperUtils.removeById(oauthOrganizationMapper, oauthOrganization);
//                }
//            }

        } catch (Exception e) {
            throw new ServiceException("500", "删除机构失败");
        }

        return true;
    }

    /**
     * @param organizationIds
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据机构Id查询机构信息
     * @date 14:49 2019/9/27
     **/
    @Override
    public List<OauthOrganizationVO> selectOrganizationByIds(@RequestBody List<Long> organizationIds) {

        if (CollectionUtils.isEmpty(organizationIds)) {
            log.info("selectOrganizationByIds -> 传入查询机构Ids为null");
            return Lists.newArrayList();
        }

        Collection<OauthOrganization> oauthOrganizations = MapperUtils.listByIds(
                oauthOrganizationMapper, organizationIds);

        List<OauthOrganizationVO> oauthOrganizationVOS = Lists.newArrayListWithCapacity(oauthOrganizations.size());

        oauthOrganizations.forEach(oauthOrganization -> {
            OauthOrganizationVO oauthOrganizationVO = new OauthOrganizationVO();
            BeanUtils.copyProperties(oauthOrganization, oauthOrganizationVO);
            oauthOrganizationVO.setOrganizationId(oauthOrganization.getId());
            oauthOrganizationVOS.add(oauthOrganizationVO);
        });

        return oauthOrganizationVOS;
    }

    /**
     * @param organizationCodes
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据code码删除机构
     * @date 15:14 2019-12-09
     **/
    @Override
    public Boolean deleteOauthOrganizationByCodes(List<String> organizationCodes) {
        if (CollectionUtils.isEmpty(organizationCodes)) {
            return false;
        }

        log.info("deleteOauthOrganizationByCodes -> organizationCodes : {}", organizationCodes);

        try {
            LambdaQueryWrapper<OauthOrganization> wrapper = new QueryWrapper<OauthOrganization>().lambda()
                    .in(OauthOrganization::getOrganizationCode, organizationCodes);

            List<OauthOrganization> organizations = MapperUtils.list(oauthOrganizationMapper, wrapper);

            if (!CollectionUtils.isEmpty(organizations)) {

                List<Long> organizationIds = organizations.stream().map(OauthOrganization::getId).collect(Collectors.toList());

                // 1. 修改用户关联机构信息
                LambdaUpdateWrapper<OauthUserOrganization> updateWrapper = new UpdateWrapper<OauthUserOrganization>().lambda()
                        .in(OauthUserOrganization::getOrganizationId, organizationIds)
                        .set(OauthUserOrganization::getOrganizationId, NumberUtils.LONG_ZERO);

                MapperUtils.update(oauthUserOrganizationMapper, null, updateWrapper);

                // 2. 删除机构信息

                for (Long organizationId : organizationIds) {
                    redisUtil.deleteValueByKey("organization:" + UserUtils.getUserDetails().getTenantId(), organizationId.toString());
                }
                MapperUtils.removeByIds(oauthOrganizationMapper, organizationIds);
            }
        } catch (Exception e) {
            throw new ServiceException("500", "删除机构失败");
        }
        return true;
    }

    /**
     * @return java.util.List<java.lang.String>
     * @author makaiyu
     * @description 获取全部机构码
     * @date 15:51 2020-01-06
     **/
    @Override
    public List<String> selectAllOrganizationCodes() {
        LambdaQueryWrapper<OauthOrganization> select = new QueryWrapper<OauthOrganization>().lambda()
                .select(OauthOrganization::getOrganizationCode);

        List<OauthOrganization> organizations = MapperUtils.list(oauthOrganizationMapper, select);

        if (CollectionUtils.isEmpty(organizations)) {
            return Lists.newArrayList();
        }

        return organizations.stream().map(OauthOrganization::getOrganizationCode).collect(Collectors.toList());
    }
}
