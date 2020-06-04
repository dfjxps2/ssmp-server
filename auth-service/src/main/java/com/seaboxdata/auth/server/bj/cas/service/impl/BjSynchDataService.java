package com.seaboxdata.auth.server.bj.cas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.seaboxdata.auth.api.dto.OauthOrganizationDTO;
import com.seaboxdata.auth.api.dto.OauthSaveUserDTO;
import com.seaboxdata.auth.api.dto.OauthUserInfoDTO;
import com.seaboxdata.auth.api.enums.ContactEnum;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.server.bj.cas.model.CasOrganize;
import com.seaboxdata.auth.server.bj.cas.model.CasUser;
import com.seaboxdata.auth.server.bj.cas.service.ICasService;
import com.seaboxdata.auth.server.bj.cas.service.ISynchDataService;
import com.seaboxdata.auth.server.bj.cas.synchrodata.CasUserUtils;
import com.seaboxdata.auth.server.dao.OauthOrganizationMapper;
import com.seaboxdata.auth.server.model.OauthOrganization;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.mq.AuthProducter;
import com.seaboxdata.auth.server.service.OauthGroupService;
import com.seaboxdata.auth.server.service.OauthOrganizationService;
import com.seaboxdata.auth.server.service.OauthRoleService;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BjSynchDataService implements ISynchDataService {

    @Autowired
    private OauthOrganizationService oauthOrganizationService;
    @Autowired
    private OauthUserService oauthUserService;
    @Autowired
    private ICasService casService;
    @Autowired
    private OauthOrganizationMapper oauthOrganizationMapper;
    @Autowired
    private AuthProducter authProducter;
    @Autowired
    private OauthRoleService oauthRoleService;
    @Autowired
    private OauthGroupService oauthGroupService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean checkLoginByCasUsername(String username) {

        //查询本地是否有该用户
        OauthUser oauthUser = oauthUserService.getByUsername(username);
        //本地为空时进行用户信息数据同步
        if (null == oauthUser) {
            CasUser casUser = casService.casUser(username);
            if (null == casUser) {
                throw new ServiceException("500", "无用户" + username + "信息");
            }

            //固定的数据同步操作人
            OauthUser adminUser = oauthUserService.getByUsername(CasUserUtils.ADMIN_USER);

            // 同步机构数据
            CasOrganize casOrganize = casUser.getCasOrganize();
            if (null != casOrganize && StringUtils.isNotBlank(casOrganize.getOrgCode())) {
                //查询本地是否存在
                LambdaQueryWrapper<OauthOrganization> oauthOrganizationLambdaQueryWrapper = new QueryWrapper<OauthOrganization>().lambda();
                oauthOrganizationLambdaQueryWrapper.eq(OauthOrganization::getOrganizationCode, casOrganize.getOrgCode());
                OauthOrganization organization = MapperUtils.getOne(oauthOrganizationMapper, oauthOrganizationLambdaQueryWrapper, true);
                if (null == organization) {
                    // 机构不存在时进行机构数据同步
                    this.synchOrgnize(adminUser);
                }
            }

            // 同步用户信息
            this.synchUsers(adminUser);
        }
        return true;
    }

    private void saveLocalOrg(List<CasOrganize> casOrganizes, Long parentId, OauthUser oauthUser) {
        OauthLoginUserVO userDetails = new OauthLoginUserVO()
                .setUserId(oauthUser.getId()).setTenantId(oauthUser.getTenantId());

        // 1.判断CAS处机构是否为空
        if (!CollectionUtils.isEmpty(casOrganizes)) {
            // 获取auth系统中全部机构码
            List<String> organizationCodes = oauthOrganizationService.selectAllOrganizationCodes();

            if (!CollectionUtils.isEmpty(organizationCodes)) {
                // 获取CAS机构码
                List<String> codes = casOrganizes.stream().map(CasOrganize::getOrgCode).collect(Collectors.toList());
                // 去除CAS以外的机构
                organizationCodes.removeAll(codes);

                // 根据code删除机构
                Boolean flag = oauthOrganizationService.deleteOauthOrganizationByCodes(organizationCodes);

                log.info("CAS同步机构删除code码:{} , 状态:{}", organizationCodes, flag);
            }
        }
        for (CasOrganize casOrganize : casOrganizes) {
            // 查询本地是否存在，且名称是否相同，名称不同，则更新名称，不存在插入数据
            LambdaQueryWrapper<OauthOrganization> organizationLambdaQueryWrapper = new QueryWrapper<OauthOrganization>().lambda();
            organizationLambdaQueryWrapper.eq(OauthOrganization::getOrganizationCode, casOrganize.getOrgCode());
            OauthOrganization oauthOrganization = MapperUtils.getOne(oauthOrganizationMapper, organizationLambdaQueryWrapper, true);

            Map<String, Object> map = Maps.newHashMap();

            OauthOrganizationDTO oauthOrganizationDTO = new OauthOrganizationDTO();
            if (null == oauthOrganization) {
                //插入一条数据
                oauthOrganizationDTO.setParentId(parentId);
                /** 机构名称 */
                oauthOrganizationDTO.setOrganizationName(casOrganize.getOrgName());
                /** 机构层级等级 */
                oauthOrganizationDTO.setLevel(1);
                /** 机构编码 */
                oauthOrganizationDTO.setOrganizationCode(casOrganize.getOrgCode());
                /** 机构编号 */
                oauthOrganizationDTO.setOrganizationNumber(1);
                /** 机构地址 */
                oauthOrganizationDTO.setOrganizationAddress("数据同步");
                /** 负责人用户Id */
                oauthOrganizationDTO.setManagerName(oauthUser.getUsername());
                oauthOrganizationService.saveOauthOrganization(oauthOrganizationDTO, userDetails.getUserId(), userDetails.getTenantId());

                LambdaQueryWrapper<OauthOrganization> queryWrapper = new QueryWrapper<OauthOrganization>().lambda()
                        .eq(OauthOrganization::getOrganizationName, casOrganize.getOrgName())
                        .eq(OauthOrganization::getParentId, parentId)
                        .eq(OauthOrganization::getOrganizationCode, casOrganize.getOrgCode());

                List<OauthOrganization> organizations = MapperUtils.list(oauthOrganizationMapper, queryWrapper);

                OauthOrganization organization;
                if (!CollectionUtils.isEmpty(organizations)) {
                    organization = organizations.get(0);

                    map.put(organization.getId().toString(), oauthOrganizationDTO);
                    // 发送机构消息
                    authProducter.saveOrganizationTag(organization.getId(),
                            organization.getOrganizationName(), oauthUser.getId(), oauthUser.getTenantId(), map);
                }
            } else {
                //更新数据
                oauthOrganization.setOrganizationName(casOrganize.getOrgName());
                BeanUtils.copyProperties(oauthOrganization, oauthOrganizationDTO);
                oauthOrganizationDTO.setManagerName(oauthUser.getUsername());
                oauthOrganizationDTO.setOrganizationId(oauthOrganization.getId());
                oauthOrganizationService.saveOauthOrganization(oauthOrganizationDTO, userDetails.getUserId(), userDetails.getTenantId());
                map.put(oauthOrganization.getId().toString(), oauthOrganizationDTO);

                // 发送机构消息
                authProducter.updateOrganizationTag(oauthOrganization.getId(),
                        oauthOrganization.getOrganizationName(), oauthUser.getId(), oauthUser.getTenantId(), map);
            }

            if (null != casOrganize.getChildOrgs() && casOrganize.getChildOrgs().size() > 0) {
                this.saveLocalOrg(casOrganize.getChildOrgs(), oauthOrganizationDTO.getOrganizationId(), oauthUser);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void synchOrgnize(OauthUser adminUser) {
        //获取CAS的机构信息
        List<CasOrganize> casOrganizes = casService.synchroOrgData();

        //将CAS获取的机构同属到本地auth中
        this.saveLocalOrg(casOrganizes, NumberUtils.LONG_ZERO, adminUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void synchUsers(OauthUser adminUser) {
        List<CasUser> casUsers = casService.synchroUsers("2000-01-01", "9999-01-01");
        List<String> casUserNames = casUsers.stream().map(CasUser::getUsername).collect(Collectors.toList());

        List<String> userNames = oauthUserService.selectAllUserName();

        if (!CollectionUtils.isEmpty(userNames)) {
            userNames.removeAll(casUserNames);
        }

        if (!CollectionUtils.isEmpty(userNames)) {
            Boolean flag = oauthUserService.deleteUserByUsername(userNames);
            log.info("同步cas时 同步删除用户 userNames:{} , 状态 flag : {}", userNames, flag);
        }

        log.info("casUsers : {} ", casUsers);

        for (CasUser caUser : casUsers) {
            OauthSaveUserDTO userDTO = new OauthSaveUserDTO();
            try {
                userDTO.setId(Long.valueOf(caUser.getUserId()));
            } catch (Exception e) {
                log.error("同步数据时，用户Id转化失败");
            }
            /** 登录账号 */
            userDTO.setUsername(caUser.getUsername());
            /** 名称 */
            userDTO.setName(caUser.getRealName());
            /** 密码 */
            userDTO.setPassword(CasUserUtils.PASSWORD);
            /** 租户ID */
            userDTO.setTenantId(adminUser.getTenantId());
            /** 用户联系方式 */
            List<OauthUserInfoDTO> oauthUserInfos = new ArrayList<>();
            oauthUserInfos.add(new OauthUserInfoDTO().setContact(ContactEnum.MOBILEPHONE).setInformation(caUser.getUserTel()).setIsPrimary(true));
            userDTO.setOauthUserInfos(oauthUserInfos);
            /** 工号 */
            userDTO.setJobNumber(caUser.getUsername());
            /** 状态: 1-可用，0-禁用 */
            if ("1".equals(caUser.getStatus()) || "00".equals(caUser.getStatus())) {
                userDTO.setEnabled(true);
            } else {
                userDTO.setEnabled(false);
            }
            /** 外包公司名称 */
//                    userDTO.setoutsourcingCompany;
            /** 外包公司电话 */
//                    userDTO.setoutsourcingPhone;
            /** 用户性别 */
//                    private Integer userSex;
            /** 用户生日 */
//                    userDTO.setuserBirthday;
            /** 用户职位 */
//                    userDTO.setposition;
            /** 用户职称 */
//                    userDTO.settitle;
            /** 个人介绍 */
//                    userDTO.setpersonalSignature;
            /** 用户头像 */
//                    userDTO.setuserAvatar;
            /** 用户地址 */
//                    userDTO.setuserAddress;
            /** 传入角色Ids */
//                    private List<Long> roleIds;
            /** 传入分组Ids */
//                    private List<Long> groupIds;

            /** 传入机构Ids */
            if (null == caUser.getCasOrganize()) {
                throw new ServiceException("500", "用户【" + caUser.getRealName() + "】无归属机构，数据同步失败，请联系管理员设置。");
            } else {
                String orgCode = caUser.getCasOrganize().getOrgCode();
                LambdaQueryWrapper<OauthOrganization> oauthOrganizationLambdaQueryWrapper = new QueryWrapper<OauthOrganization>().lambda();
                oauthOrganizationLambdaQueryWrapper.eq(OauthOrganization::getOrganizationCode, orgCode);
                OauthOrganization organization = MapperUtils.getOne(oauthOrganizationMapper, oauthOrganizationLambdaQueryWrapper, true);
                userDTO.setOrganizationIds(Arrays.asList(organization.getId()));

                if (Objects.nonNull(userDTO.getId())) {
                    List<Long> roleIds = oauthRoleService.selectAllRoleByUserId(userDTO.getId());
                    userDTO.setRoleIds(roleIds);

                    List<Long> groupIds = oauthGroupService.selectGroupIdByUserId(userDTO.getId());
                    userDTO.setGroupIds(groupIds);
                }

                oauthUserService.handleUserInfo(userDTO, adminUser.getTenantId());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void synchData() {
        //固定的数据同步操作人
        OauthUser adminUser = oauthUserService.getByUsername(CasUserUtils.ADMIN_USER);
        this.synchOrgnize(adminUser);
        this.synchUsers(adminUser);
    }
}
