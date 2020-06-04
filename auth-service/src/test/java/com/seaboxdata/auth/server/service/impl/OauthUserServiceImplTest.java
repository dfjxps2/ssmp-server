package com.seaboxdata.auth.server.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.*;
import com.seaboxdata.auth.api.enums.ContactEnum;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.auth.server.sync.service.ISyncInternalService;
import com.seaboxdata.commons.query.PaginationResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Slf4j
@ActiveProfiles("mky")
public class OauthUserServiceImplTest extends AuthApplicationTests {

    @Autowired
    private OauthUserService oauthUserService;

    @Autowired
    protected ISyncInternalService syncInternalService;

    @Test
    public void getByUsername() {
    }

    @Test
    public void syncUser() {
        OauthUserNamePageDTO pageDTO = new OauthUserNamePageDTO();
//        pageDTO.setOffset(1);
//        pageDTO.setLimit(10);
//        pageDTO.setGroupId(1L);
        pageDTO.setOrganizationId(0L);
        PaginationResult<OauthUserVO> result = syncInternalService.getAllUser(pageDTO);

        log.info("result : {}", result);
    }

    @Test
    public void registerUser() {
        OauthSaveUserDTO sysUser = new OauthSaveUserDTO();
        sysUser.setName("小花2");
        sysUser.setPassword("123123");
        sysUser.setEnabled(true);
        sysUser.setUsername("admin5");

        List<OauthUserInfoDTO> oauthUserInfos = Lists.newArrayList();
        OauthUserInfoDTO oauthUserInfoDTO = new OauthUserInfoDTO();
        oauthUserInfoDTO.setContact(ContactEnum.MAILBOX);
        oauthUserInfoDTO.setInformation("1377yy@dd.com");
        oauthUserInfoDTO.setIsPrimary(true);

        OauthUserInfoDTO oauthUserInfoDTO1 = new OauthUserInfoDTO();
        oauthUserInfoDTO1.setContact(ContactEnum.FIXEDTELEPHONE);
        oauthUserInfoDTO1.setInformation("13778877673");
        oauthUserInfoDTO1.setIsPrimary(true);

        OauthUserInfoDTO oauthUserInfoDTO2 = new OauthUserInfoDTO();
        oauthUserInfoDTO2.setContact(ContactEnum.MOBILEPHONE);
        oauthUserInfoDTO2.setInformation("137784384");
        oauthUserInfoDTO2.setIsPrimary(true);

        oauthUserInfos.add(oauthUserInfoDTO);
        oauthUserInfos.add(oauthUserInfoDTO1);
        oauthUserInfos.add(oauthUserInfoDTO2);

        sysUser.setOauthUserInfos(oauthUserInfos);
        Boolean aBoolean = oauthUserService.registerUser(sysUser);
        log.info("oauthUser : {}", aBoolean);
    }

    @Test
    public void deleteByUserId() {
        Boolean flag = oauthUserService.deleteUserById(Lists.newArrayList(135769018442321920L, 135767546879152128L));

        log.info("flag : {}", flag);
    }

    @Test
    public void findAllUser() {
        OauthUserNamePageDTO pageDTO = new OauthUserNamePageDTO();
//        pageDTO.setOffset(1);
//        pageDTO.setLimit(10);
//        pageDTO.setGroupId(1L);
        pageDTO.setOrganizationId(0L);
//        pageDTO.setName("曾");
        PaginationResult<OauthUserVO> oauthUserVOPaginationResult = oauthUserService.selectAllUser(pageDTO);

        log.info("oauthUserDTOPage : {}", oauthUserVOPaginationResult);
    }

    @Test
    public void test89() {

        OauthUser user = oauthUserService.getByUsername("admin");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // 加密
        String encodedPassword = passwordEncoder.encode(user.getPassword().trim());
        user.setPassword(encodedPassword);

//        oauthUserService.saveOrUpdate(user);
    }

    @Test
    public void logout() {
        oauthUserService.logout();
    }

    @Test
    public void updateOauthUser() {
        OauthSaveUserDTO oauthUserDTO = new OauthSaveUserDTO();
        oauthUserDTO.setId(96907470886277120L);
        oauthUserService.updateOauthUser(oauthUserDTO);
    }

    @Test
    public void selectUserByUserId() {
        List<OauthUserVO> oauthUserVOS = oauthUserService.selectUserByUserId(
                Lists.newArrayList(122745844440109056L));
        log.info("user : {} ", oauthUserVOS);
    }

    @Test
    public void selectUserByRoleId() {
        List<OauthUserVO> oauthUserVOS = oauthUserService.selectUserByRoleId(2L);
        log.info("aouthUser: {}", oauthUserVOS);
    }

    @Test
    public void checkUserName() {
        OauthUserDTO user = new OauthUserDTO();
        Boolean result = oauthUserService.checkUserName(user);
        log.info("result : {} ", result);
    }

    @Test
    public void selectUserByNameOrCard() {
        AuthUserParamDTO authUserParamDTO = new AuthUserParamDTO();
        authUserParamDTO.setKeyWords("10002");
        List<OauthUserVO> oauthUserVOS = oauthUserService.authSelectUserByNameOrCard(authUserParamDTO);
        log.info("result : {} ", oauthUserVOS);
    }

    @Test
    public void updateUserPwd() {
        PwdDTO pwdDTO = new PwdDTO();
        pwdDTO.setNewPwd("123123")
                .setOriginalPwd("1231234");
        Boolean flag = oauthUserService.updateUserPwd(pwdDTO);
    }


}