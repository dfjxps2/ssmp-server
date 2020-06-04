package com.seaboxdata.auth.server.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.OauthOrganizationDTO;
import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.vo.OauthOrganizationVO;
import com.seaboxdata.auth.server.service.OauthOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Slf4j
@ActiveProfiles("mky")
public class OauthOrganizationServiceImplTest extends AuthApplicationTests {

    @Autowired
    private OauthOrganizationService oauthOrganizationService;

    @Test
    public void saveOauthOrganization() {

        OauthOrganizationDTO oauthOrganizationDTO = new OauthOrganizationDTO();
        oauthOrganizationDTO.setOrganizationName("财务3子机构1")
                .setManagerMail("caiwu.com")
                .setManagerPhone("1234")
                .setOrganizationAddress("商民2路2")
                .setOrganizationNumber(1022)
                .setManagerName("jiahualong")
                .setParentId(100187348754632704L);
        oauthOrganizationService.saveOauthOrganization(oauthOrganizationDTO, 1L, 1L);
    }

    @Test
    public void selectAllOrganization() {
        OauthOrganizationParamDTO oauthOrganizationParamDTO = new OauthOrganizationParamDTO();

//        oauthOrganizationParamDTO.setKeyWords("商");
        List<OauthOrganizationVO> oauthOrganizationVOS = oauthOrganizationService.selectAllOrganization(oauthOrganizationParamDTO);
        log.info("oauthOrganizationVOS : {}", oauthOrganizationVOS);
    }

    @Test
    public void deleteOrganization() {
        Boolean organization = oauthOrganizationService.deleteOauthOrganization(
                Lists.newArrayList(113949795424538624L));
        log.info("organization : {} ", organization);
    }

    @Test
    public void selectOrganizationByIds() {
        List<OauthOrganizationVO> oauthOrganizationVOS = oauthOrganizationService
                .selectOrganizationByIds(Lists.newArrayList(133295121805479936L, 137254985225342976L));

        log.info("result : {} ", oauthOrganizationVOS);
    }

    @Test
    public void selectOrganizationByUserId() {
        List<OauthOrganizationVO> oauthOrganizationVOS = oauthOrganizationService.selectOrganizationByUserId(0L);

        log.info("oauthOrganizationVOS : {}", oauthOrganizationVOS);
    }

}