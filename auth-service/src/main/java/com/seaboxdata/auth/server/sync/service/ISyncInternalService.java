package com.seaboxdata.auth.server.sync.service;

import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.dto.OauthUserNamePageDTO;
import com.seaboxdata.auth.api.vo.OauthOrganizationVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.commons.query.PaginationResult;

import java.util.List;

/**
 * @author ：long
 * @date ：Created in 2020/2/27 下午5:29
 * @description：
 */

public interface ISyncInternalService {

    /**
     * query all user by organizationId, if organizationId is null then get all user
     *
     * @param pageDTO
     * @return
     */
    PaginationResult<OauthUserVO> getAllUser(OauthUserNamePageDTO pageDTO);


    /**
     * get all organization
     * @param oauthOrganizationParamDTO
     * @return
     */
    List<OauthOrganizationVO> getAllOrganization(OauthOrganizationParamDTO oauthOrganizationParamDTO);


    /**
     * 获取所有用户，根据机构
     */
    List<OauthUser> queryAllUser(Long orgId);

}
