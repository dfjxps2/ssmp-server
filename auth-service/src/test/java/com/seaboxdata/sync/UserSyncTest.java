package com.seaboxdata.sync;

import com.seaboxdata.AuthApplicationTests;
import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.dto.OauthUserNamePageDTO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.sync.service.ISyncInternalService;
import com.seaboxdata.commons.query.PaginationResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author ：long
 * @date ：Created in 2020/2/28 下午12:27
 * @description：
 */
@Slf4j
@ActiveProfiles("long")
public class UserSyncTest extends AuthApplicationTests {

    @Autowired
    private ISyncInternalService syncInternalService;

    @Test
    public void allUser() {
        OauthUserNamePageDTO oauthUserNamePageDTO = new OauthUserNamePageDTO();
        oauthUserNamePageDTO.setOffset(1);
        oauthUserNamePageDTO.setLimit(5);
//        oauthUserNamePageDTO.setOrganizationId(137254985225342976L);
        PaginationResult<OauthUserVO> allUser = syncInternalService.getAllUser(oauthUserNamePageDTO);
        log.info("total: {}", allUser.getTotal());
        log.info("limit: {}", allUser.getLimit());
        log.info("offset: {}", allUser.getOffset());
        allUser.getData().forEach(System.out::println);
    }

    @Test
    public void allOrganizations() {
        OauthOrganizationParamDTO paramDTO = new OauthOrganizationParamDTO();

        syncInternalService.getAllOrganization(paramDTO).forEach(System.out::println);
    }

}
