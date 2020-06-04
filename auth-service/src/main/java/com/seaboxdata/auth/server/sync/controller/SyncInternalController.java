package com.seaboxdata.auth.server.sync.controller;

import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.dto.OauthUserNamePageDTO;
import com.seaboxdata.auth.api.vo.OauthOrganizationVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.sync.service.ISyncInternalService;
import com.seaboxdata.commons.query.PaginationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author ：long
 * @date ：Created in 2020/2/27 下午3:24
 * @description：
 */

@RestController
public class SyncInternalController {


    private ISyncInternalService syncInternalService;

    /**
     * user page query
     *
     * @param pageDTO
     * @return
     */
    @PostMapping("/sync/getAllUser")
    public PaginationResult<OauthUserVO> getAllUser(@RequestBody OauthUserNamePageDTO pageDTO) {
        return syncInternalService.getAllUser(pageDTO);
    }

    /**
     * 获取所有用户 非分页查询
     * @param orgId
     * @return
     */
    @GetMapping("/sync/queryAllUser")
    public List<OauthUser> queryAllUser(@RequestParam(required = false) Long orgId) {
        return syncInternalService.queryAllUser(orgId);
    }


    /**
     * get all organization
     *
     * @param oauthOrganizationParamDTO
     * @return
     */
    @PostMapping("/sync/getAllOrganization")
    public List<OauthOrganizationVO> getAllOrganization(@RequestBody(required = false) OauthOrganizationParamDTO oauthOrganizationParamDTO) {
        if (Objects.isNull(oauthOrganizationParamDTO)) {
            oauthOrganizationParamDTO = new OauthOrganizationParamDTO();
        }
        return syncInternalService.getAllOrganization(oauthOrganizationParamDTO);
    }


    @Autowired
    public SyncInternalController(ISyncInternalService syncInternalService) {
        this.syncInternalService = syncInternalService;
    }
}
