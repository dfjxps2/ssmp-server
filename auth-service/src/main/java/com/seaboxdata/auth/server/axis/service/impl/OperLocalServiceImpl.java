package com.seaboxdata.auth.server.axis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seaboxdata.auth.api.dto.OauthOrganizationDTO;
import com.seaboxdata.auth.api.dto.OauthSaveUserDTO;
import com.seaboxdata.auth.api.dto.OauthUserInfoDTO;
import com.seaboxdata.auth.api.enums.ContactEnum;
import com.seaboxdata.auth.server.axis.model.OrgInfo;
import com.seaboxdata.auth.server.axis.model.RoleInfo;
import com.seaboxdata.auth.server.axis.model.UserInfo;
import com.seaboxdata.auth.server.axis.service.IAxisSynService;
import com.seaboxdata.auth.server.axis.service.OperLocalService;
import com.seaboxdata.auth.server.axis.utils.CaEncryptUtils;
import com.seaboxdata.auth.server.axis.utils.OperTypeUtils;
import com.seaboxdata.auth.server.bj.cas.synchrodata.CasUserUtils;
import com.seaboxdata.auth.server.dao.OauthOrganizationMapper;
import com.seaboxdata.auth.server.dao.OauthUserMapper;
import com.seaboxdata.auth.server.dao.OauthUserOrganizationMapper;
import com.seaboxdata.auth.server.model.OauthOrganization;
import com.seaboxdata.auth.server.model.OauthTranCode;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.model.OauthUserOrganization;
import com.seaboxdata.auth.server.service.OauthOrganizationService;
import com.seaboxdata.auth.server.service.OauthRoleService;
import com.seaboxdata.auth.server.service.OauthTranCodeService;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.auth.server.utils.EncryptUtil;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class OperLocalServiceImpl implements OperLocalService {

    @Autowired
    private OauthTranCodeService oauthTranCodeService;
    @Autowired
    private IAxisSynService axisSynService;
    @Autowired
    private OauthUserService oauthUserService;
    @Autowired
    private OauthOrganizationService oauthOrganizationService;
    @Autowired
    private OauthRoleService oauthRoleService;
    @Autowired
    private OauthOrganizationMapper oauthOrganizationMapper;
    @Autowired
    private OauthUserOrganizationMapper oauthUserOrganizationMapper;
    @Autowired
    private OauthUserMapper oauthUserMapper;

    @Value("${axis.ca.service.appId}")
    private String caAppId;

    private String saveOrg(OrgInfo orgInfo, Long orgId, Long parentId){
        /**
         * 保存机构
         */
        //固定的数据同步操作人
        OauthUser adminUser = oauthUserService.getByUsername(CasUserUtils.ADMIN_USER);

        OauthOrganizationDTO oauthOrganizationDTO = new OauthOrganizationDTO();
        oauthOrganizationDTO.setOrganizationId(orgId);
        //插入一条数据
        oauthOrganizationDTO.setParentId(parentId == null? NumberUtils.LONG_ZERO : parentId);
        /** 机构名称 */
        oauthOrganizationDTO.setOrganizationName(orgInfo.getName());
        /** 机构层级等级 */
        oauthOrganizationDTO.setLevel(1);
        /** 机构编码 */
        oauthOrganizationDTO.setOrganizationCode(orgInfo.getOrgCode());
        /** 机构编号 */
        oauthOrganizationDTO.setOrganizationNumber(1);
        /** 机构地址 */
        oauthOrganizationDTO.setOrganizationAddress(OperTypeUtils.OrganizationAddress);
        /** 负责人用户Id */
        oauthOrganizationDTO.setManagerName(adminUser.getUsername());

        if(this.orgIsExistByName(oauthOrganizationDTO.getParentId(), oauthOrganizationDTO.getOrganizationName(), orgId)){
            if(null == orgId){
                //103：增加组织时同级组织名称重复
                return OperTypeUtils.DEPT_ADD_NAME_REP;
            }else{
                //303：修改组织时同级组织名称重复
                return OperTypeUtils.DEPT_UPDATE_NAME_REP;
            }
        }

        /**
         * 保存
         */
        oauthOrganizationService.saveOauthOrganization(oauthOrganizationDTO, adminUser.getId(), adminUser.getTenantId());

        /**
         * 保存本地中间表
         */
        if(null == orgId){
            oauthTranCodeService.insertOauthTranCode(new OauthTranCode().setCaCode(orgInfo.getOrgCode()).setLocalId(oauthOrganizationDTO.getOrganizationId()).setType(OperTypeUtils.TRAN_DEPT));
        }
        return OperTypeUtils.CA_RQUEST_SUCCESS;
    }

    private Boolean orgIsExistByName(Long parentId, String name, Long orgId){
        LambdaQueryWrapper<OauthOrganization> lambdaQueryWrapper = new QueryWrapper<OauthOrganization>().lambda();
        lambdaQueryWrapper.eq(OauthOrganization::getParentId, parentId);
        lambdaQueryWrapper.eq(OauthOrganization::getOrganizationName, name);
        List<OauthOrganization> list = oauthOrganizationMapper.selectList(lambdaQueryWrapper);
        if(!list.isEmpty() && list.size() > 0){
            if(null == orgId){
                //新增时已存在重名
                return true;
            }else {
                for(OauthOrganization item : list){
                    if(orgId.longValue() != item.getId()){
                        //更新时除了自己还存在重名
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String addOrg(String orgCode) {
        /**
         * 查询本地是否存在
         */
        Long orgId = oauthTranCodeService.queryLocalIdByTranCode(orgCode, OperTypeUtils.TRAN_DEPT);
        if(null != orgId){
            //101：增加组织唯一标识重复
            return OperTypeUtils.DEPT_ADD_CODE_REP;
        }
        /**
         * 本地不存在，需请求ca获取并添加
         */
        OrgInfo reqOrgInfo = axisSynService.xmlToOrgInfo(axisSynService.getDeptInfo(orgCode));

        /**
         * 上级是否存在
         */
        Long parentId = null;
        if(StringUtils.isNotBlank(reqOrgInfo.getParentCode()) && !OperTypeUtils.UP_CODE.equals(reqOrgInfo.getParentCode())){
            //获取上级机构id
            parentId = oauthTranCodeService.queryLocalIdByTranCode(reqOrgInfo.getParentCode(), OperTypeUtils.TRAN_DEPT);
            if(null == parentId){
                //102：增加组织的上级节点不存在
                return OperTypeUtils.DEPT_ADD_NODE_NON;
            }
        }
        return this.saveOrg(reqOrgInfo, null, parentId);
    }

    @Override
    public String addRole(String roleCode) {

        //7．增加角色
        //701：增加角色唯一标识重复
        //702：增加角色名字重复
        //703：增加角色的组织标识编号不存在
//        public static final String ROLE_ADD_CODE_REP = "701";
//        public static final String ROLE_ADD_NAME_REP = "702";
//        public static final String ROLE_ADD_CODE_NON = "703";

        /**
         * 查询本地是否存在
         */
        Long roleId = oauthTranCodeService.queryLocalIdByTranCode(roleCode, OperTypeUtils.TRAN_ROLE);
        if(null != roleId){
            return OperTypeUtils.ROLE_ADD_CODE_REP;
        }

        /**
         * 本地不存在，需请求ca获取并添加
         */
        RoleInfo reqRoleInfo = axisSynService.xmlToRoleInfo(axisSynService.getRoleInfo(roleCode));

        /**
         * 查询归属的机构，本地是否存在，不存在需要同步
         */
        reqRoleInfo.getOrgCode();


        /**
         * 文档中只涉及到角色名称和归属的机构orgcode，这些信息对现auth几乎没什么作用，暂时同步，待后续完善
         */
        //同步角色

        return OperTypeUtils.CA_RQUEST_SUCCESS;
    }


    @Override
    public String addUser(String userCode) {

        //4．增加用户

        /**
         * 查询本地是否存在
         */
        Long userId = oauthTranCodeService.queryLocalIdByTranCode(userCode, OperTypeUtils.TRAN_USER);

        /**
         * 由于之前的用户中间表处理有问题，造成中间表数据不存在，此处不做判断
         */
//        if(null != userId){
//            //401：增加用户唯一标识重复
//            return OperTypeUtils.USER_ADD_CODE_REP;
//        }

        /**
         * 本地不存在，需请求ca获取并添加
         */
        UserInfo reqUserInfo = axisSynService.xmlToUserInfo(axisSynService.getUserInfo(userCode, caAppId));

        //402：增加用户的角色标识编号不存在
        reqUserInfo.getRoleList();
//        OperTypeUtils.USER_ADD_ROLE_NON;

        Long orgId = oauthTranCodeService.queryLocalIdByTranCode(reqUserInfo.getOrgCode(), OperTypeUtils.TRAN_DEPT);
        if(null == orgId){
            //403：增加用户的组织标识编号不存在
            return OperTypeUtils.USER_ADD_DEPT_NON;
        }

        return this.saveUser(reqUserInfo, userId, orgId, "add");
    }

    private String saveUser(UserInfo reqUserInfo, Long userId, Long orgId, String czType){

        OauthSaveUserDTO userDTO = new OauthSaveUserDTO();

        /**
         * 获取归属机构本地id，暂时角色不做同步，暂时不做角色的处理
         */
        reqUserInfo.getRoleList();

        //固定的数据同步操作人
        OauthUser adminUser = oauthUserService.getByUsername(CasUserUtils.ADMIN_USER);

        if(null != userId){
            LambdaQueryWrapper<OauthUser> oauthUserLambdaQueryWrapper = new QueryWrapper<OauthUser>().lambda();
            oauthUserLambdaQueryWrapper.eq(OauthUser::getTenantId, adminUser.getTenantId());
            oauthUserLambdaQueryWrapper.eq(OauthUser::getUsername, reqUserInfo.getUserLoginName());
            List<OauthUser> oauthUserList = oauthUserMapper.selectList(oauthUserLambdaQueryWrapper);
            if(!oauthUserList.isEmpty() && oauthUserList.size() > 0){
                /** 传入机构Ids */
                userDTO.setId(oauthUserList.get(0).getId());
            }
        }else{
            userDTO.setId(userId);
        }

        if("add".equals(czType)){
            if(null != userDTO.getId()){
                //401：增加用户唯一标识重复
                return OperTypeUtils.USER_ADD_CODE_REP;
            }
        }else if("update".equals(czType)){
            if(null == userDTO.getId()){
                //601：用户唯一标识对应的用户不存在
                return OperTypeUtils.USER_DEL_CODE_NON;
            }
        }

        /** 登录账号 */
        userDTO.setUsername(reqUserInfo.getUserLoginName());
        /** 名称 */
        userDTO.setName(reqUserInfo.getName());
        /** 密码 */
//        String password = CaEncryptUtils.ssoEncrypt(reqUserInfo.getSynpassword(), false);
        userDTO.setPassword(CasUserUtils.PASSWORD);
        /** 租户ID */
        userDTO.setTenantId(adminUser.getTenantId());
        /** 用户联系方式 */
        List<OauthUserInfoDTO> oauthUserInfos = new ArrayList<>();
        oauthUserInfos.add(new OauthUserInfoDTO().setContact(ContactEnum.MOBILEPHONE).setInformation(reqUserInfo.getMobilePhone()).setIsPrimary(true));
        userDTO.setOauthUserInfos(oauthUserInfos);

        /** 工号 */
        userDTO.setJobNumber(reqUserInfo.getUserLoginName());
        /** 状态: 1-可用，0-禁用 */
        userDTO.setEnabled(true);
        /** 外包公司名称 */
//                    userDTO.setoutsourcingCompany;
        /** 外包公司电话 */
//                    userDTO.setoutsourcingPhone;
        /** 用户性别 */
        userDTO.setUserSex(Integer.valueOf(reqUserInfo.getSex()));
        /** 用户生日 */
//                    userDTO.setuserBirthday;
        /** 用户职位 */
        userDTO.setPosition(reqUserInfo.getUserDuty());
        /** 用户职称 */
        userDTO.setTitle(reqUserInfo.getUserPost());
        /** 个人介绍 */
//                    userDTO.setpersonalSignature;
        /** 用户头像 */
//                    userDTO.setuserAvatar;
        /** 用户地址 */
        userDTO.setUserAddress(reqUserInfo.getAddress());
        /** 传入角色Ids */
//                    private List<Long> roleIds;
        /** 传入分组Ids */
//                    private List<Long> groupIds;

        userDTO.setOrganizationIds(Arrays.asList(orgId));

        /** 是否为系统管理员 大兴 */
        userDTO.setSystemManager(false);

        Long createUserId = oauthUserService.handleUserInfo(userDTO, adminUser.getTenantId());

        /**
         * 保存本地中间表
         */

        //判断中间表数据是否存在
        Long localId = oauthTranCodeService.queryLocalIdByTranCode(reqUserInfo.getUserCode(), OperTypeUtils.TRAN_USER);
        if(null == localId){
            oauthTranCodeService.insertOauthTranCode(new OauthTranCode().setCaCode(reqUserInfo.getUserCode()).setLocalId(createUserId).setType(OperTypeUtils.TRAN_USER));
        }
        return OperTypeUtils.CA_RQUEST_SUCCESS;
    }

    @Override
    public String delOrg(String orgCode) {

        //2．删除组织
        Long orgId = oauthTranCodeService.queryLocalIdByTranCode(orgCode, OperTypeUtils.TRAN_DEPT);
        if(null == orgId){
            //201：组织唯一标识对应的组织不存在
            return OperTypeUtils.DEPT_DEL_CODE_NON;
        }

        //202：删除的组织下存在子组织
        LambdaQueryWrapper<OauthOrganization> oauthOrganizationLambdaQueryWrapper = new QueryWrapper<OauthOrganization>().lambda();
        oauthOrganizationLambdaQueryWrapper.eq(OauthOrganization::getParentId, orgId);
        List<OauthOrganization> oauthOrganizationList = oauthOrganizationMapper.selectList(oauthOrganizationLambdaQueryWrapper);
        if(!oauthOrganizationList.isEmpty() && oauthOrganizationList.size() > 0){
            return OperTypeUtils.DEPT_DEL_EXIST_NODE;
        }


        LambdaQueryWrapper<OauthUserOrganization> oauthUserOrganizationLambdaQueryWrapper = new QueryWrapper<OauthUserOrganization>().lambda();
        oauthUserOrganizationLambdaQueryWrapper.eq(OauthUserOrganization::getOrganizationId, orgId);
        List<OauthUserOrganization> oauthUserOrganizationList = oauthUserOrganizationMapper.selectList(oauthUserOrganizationLambdaQueryWrapper);
        if(!oauthUserOrganizationList.isEmpty() && oauthUserOrganizationList.size() > 0){
            //203：删除的组织下存在用户
            return OperTypeUtils.DEPT_DEL_EXIST_USER;
        }


        //204：删除的组织下存在角色
        //OperTypeUtils.DEPT_DEL_EXIST_ROLE = "204";

        /**
         * 删除本地中间表
         */
        oauthTranCodeService.deleteOauthTranCodeByCaCode(orgCode, OperTypeUtils.TRAN_DEPT);

        /**
         * 删除机构
         */
        oauthOrganizationService.deleteOauthOrganization(Arrays.asList(orgId));

        return OperTypeUtils.CA_RQUEST_SUCCESS;
    }

    @Override
    public String delRole(String roleCode) {

//        Long roleId = oauthTranCodeService.queryLocalIdByTranCode(roleCode, OperTypeUtils.TRAN_ROLE);
//        if(null != roleId){
//            oauthRoleService.deleteOauthRole(Arrays.asList(roleId));
//        }
//
//        /**
//         * 删除本地中间表
//         */
//        oauthTranCodeService.deleteOauthTranCodeByCaCode(roleCode, OperTypeUtils.TRAN_ROLE);

        return OperTypeUtils.CA_RQUEST_SUCCESS;
    }

    @Override
    public String delUser(String userCode) {
        //6．删除用户
        Long userId = oauthTranCodeService.queryLocalIdByTranCode(userCode, OperTypeUtils.TRAN_USER);
        if(null == userId){
            //601：用户唯一标识对应的用户不存在
            return OperTypeUtils.USER_DEL_CODE_NON;
        }

        /**
         * 删除本地中间表
         */
        oauthTranCodeService.deleteOauthTranCodeByCaCode(userCode, OperTypeUtils.TRAN_USER);

        /**
         * 删除本机user
         */
        oauthUserService.deleteUserById(Arrays.asList(userId));

        return OperTypeUtils.CA_RQUEST_SUCCESS;
    }

    @Override
    public String updateOrg(String orgCode) {

        /**
         * 查询本地是否存在
         */
        Long orgId = oauthTranCodeService.queryLocalIdByTranCode(orgCode, OperTypeUtils.TRAN_DEPT);
        if(null == orgId){
            //301：组织唯一标识对应的组织不存在
            return OperTypeUtils.DEPT_UPDATE_CODE_NON;

            //新增一个
            //return this.addOrg(orgCode);
        }

        OrgInfo reqOrgInfo = axisSynService.xmlToOrgInfo(axisSynService.getDeptInfo(orgCode));

        /**
         * 上级是否存在
         */
        Long parentId = null;
        if(StringUtils.isNotBlank(reqOrgInfo.getParentCode()) && !OperTypeUtils.UP_CODE.equals(reqOrgInfo.getParentCode())){
            //获取上级机构id
            parentId = oauthTranCodeService.queryLocalIdByTranCode(reqOrgInfo.getParentCode(), OperTypeUtils.TRAN_DEPT);
            if(null == parentId){
                //302：修改组织的上级节点不存在
                return OperTypeUtils.DEPT_UPDATE_UP_NON;
            }
        }

        return this.saveOrg(reqOrgInfo, orgId, parentId);
    }

    @Override
    public String updateRole(String roleCode) {

        return OperTypeUtils.CA_RQUEST_SUCCESS;
    }

    @Override
    public String updateUser(String userCode) {

        //5．修改用户

        /**
         * 查询本地是否存在
         */
        Long userId = oauthTranCodeService.queryLocalIdByTranCode(userCode, OperTypeUtils.TRAN_USER);

        /**
         * 由于之前的用户中间表处理有问题，造成中间表数据不存在，此处不做判断
         */
//        if(null == userId){
//            //501：用户唯一标识对应的用户不存在
//            return OperTypeUtils.USER_UPDATE_CODE_NON;
//        }

        /**
         * 需请求ca获取并修改
         */
        UserInfo reqUserInfo = axisSynService.xmlToUserInfo(axisSynService.getUserInfo(userCode, caAppId));

        //502：修改用户的角色标识编号不存在
        reqUserInfo.getRoleList();
//        OperTypeUtils.USER_UPDATE_ROLE_NON;

        Long orgId = oauthTranCodeService.queryLocalIdByTranCode(reqUserInfo.getOrgCode(), OperTypeUtils.TRAN_DEPT);
        if(null == orgId){
            //503：修改用户的组织标识编号不存在
            return OperTypeUtils.USER_UPDATE_DEPT_NON;
        }

        return this.saveUser(reqUserInfo, userId, orgId, "update");
    }
}
