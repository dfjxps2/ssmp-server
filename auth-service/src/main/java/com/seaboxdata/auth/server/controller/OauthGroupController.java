package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthGroupController;
import com.seaboxdata.auth.api.dto.OauthGroupDTO;
import com.seaboxdata.auth.api.dto.OauthGroupParamDTO;
import com.seaboxdata.auth.api.dto.OauthUserGroupDTO;
import com.seaboxdata.auth.api.vo.OauthGroupVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.service.OauthGroupService;
import com.seaboxdata.commons.query.PaginationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
@Service
public class OauthGroupController implements IOauthGroupController {

    @Autowired
    private OauthGroupService oauthGroupService;

    /**
     * @param oauthGroupDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 增加分组信息
     * @date 15:11 2019/5/31
     **/
    @Override
    public Boolean saveOauthGroup(@RequestBody OauthGroupDTO oauthGroupDTO) {
        return oauthGroupService.saveOauthGroup(oauthGroupDTO);
    }


    /**
     * @param oauthGroupParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthGroupVO>
     * @author makaiyu
     * @description 查询全部分组
     * @date 15:19 2019/5/31
     */
    @Override
    public List<OauthGroupVO> selectAllGroup(@RequestBody OauthGroupParamDTO oauthGroupParamDTO) {
        return oauthGroupService.selectAllGroup(oauthGroupParamDTO);
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
        return oauthGroupService.deleteOauthGroup(groupIds);
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
        return oauthGroupService.selectUserByGroupId(oauthGroupDTO);
    }

    /**
     * @param oauthUserGroupDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个分组 -> 多个用户
     * @date 14:29 2020-04-26
     **/
    @Override
    public Boolean saveOrUpdateUserGroup(@RequestBody OauthUserGroupDTO oauthUserGroupDTO) {
        return oauthGroupService.saveOrUpdateUserGroup(oauthUserGroupDTO);
    }
}
