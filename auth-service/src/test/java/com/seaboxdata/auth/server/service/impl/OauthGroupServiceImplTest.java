package com.seaboxdata.auth.server.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.OauthGroupDTO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.service.OauthGroupService;
import com.seaboxdata.commons.query.PaginationResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class OauthGroupServiceImplTest extends AuthApplicationTests {

    @Autowired
    private OauthGroupService oauthGroupService;

    @Test
    public void saveOauthGroup() {
        OauthGroupDTO oauthGroupDTO = new OauthGroupDTO();
        oauthGroupDTO.setGroupName("慈善机构1")
                .setGroupDesc("一家专门做慈善的机构1")
                .setManagerMail("13787@126.com1")
                .setManagerName("huyifei")
                .setManagerPhone("138776655519");
        oauthGroupService.saveOauthGroup(oauthGroupDTO);
    }

    @Test
    public void selectAllGroup() {
//        List<OauthGroupVO> oauthGroupVOS = oauthGroupService.selectAllGroup(oauthGroupParamDTO);
//        log.info("result: {} ", oauthGroupVOS);
    }

    @Test
    public void deleteOauthGroup() {
        Boolean flag = oauthGroupService.deleteOauthGroup(Lists.newArrayList(113947140182642688L));
        log.info("flag : {} ", flag);
    }

    @Test
    public void selectUserByGroupId() {
        OauthGroupDTO oauthGroupDTO = new OauthGroupDTO();
        oauthGroupDTO.setGroupId(1L);
        oauthGroupDTO.setOffset(1);
        oauthGroupDTO.setLimit(2);
        PaginationResult<OauthUserVO> paginationResult = oauthGroupService.selectUserByGroupId(oauthGroupDTO);
        log.info("oauthUser : {} ", paginationResult);
    }


}