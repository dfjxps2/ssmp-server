package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IOauthGroupController;
import com.seaboxdata.auth.api.dto.OauthGroupDTO;
import com.seaboxdata.auth.api.dto.OauthGroupParamDTO;
import com.seaboxdata.auth.api.dto.OauthUserGroupDTO;
import com.seaboxdata.auth.api.vo.OauthGroupVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.resolver.input.AuthGroupInput;
import com.seaboxdata.auth.resolver.input.AuthGroupSaveInput;
import com.seaboxdata.commons.query.PaginationResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author makaiyu
 * @date 2019/5/31 15:41
 */
@Service
public class AuthGroupResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IOauthGroupController oauthGroupController;

    @Autowired
    public AuthGroupResolver(IOauthGroupController oauthGroupController) {
        this.oauthGroupController = oauthGroupController;
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthGroupVO>
     * @author makaiyu
     * @description 查询全部分组
     * @date 15:19 2019/5/31
     **/
    public List<OauthGroupVO> selectAllGroup(AuthGroupInput authGroupInput) {
        OauthGroupParamDTO oauthGroupParamDTO = new OauthGroupParamDTO();
        if (Objects.nonNull(authGroupInput)) {
            BeanUtils.copyProperties(authGroupInput, oauthGroupParamDTO);
        }
        return oauthGroupController.selectAllGroup(oauthGroupParamDTO);
    }

    /**
     * @param authGroupSaveInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 增加/修改分组信息
     * @date 15:11 2019/5/31
     **/
    @PreAuthorize("hasAuthority('ucManager')")
    public Boolean saveOrUpdateOauthGroup(AuthGroupSaveInput authGroupSaveInput) {

        OauthGroupDTO oauthGroupDTO = new OauthGroupDTO();
        BeanUtils.copyProperties(authGroupSaveInput, oauthGroupDTO);
        return oauthGroupController.saveOauthGroup(oauthGroupDTO);
    }

    /**
     * @param groupIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据Id 删除分组
     * @date 15:26 2019/5/31
     **/
    @PreAuthorize("hasAuthority('ucManager')")
    public Boolean deleteOauthGroup(List<Long> groupIds) {
        return oauthGroupController.deleteOauthGroup(groupIds);
    }

    /**
     * @param authGroupInput
     * @return com.seaboxdata.auth.api.vo.OauthGroupVO
     * @author makaiyu
     * @description 根据分组Id  获取组内成员
     * @date 15:39 2019/6/3
     **/
    public PaginationResult<OauthUserVO> authSelectUserByGroupId(AuthGroupInput authGroupInput) {
        OauthGroupDTO oauthGroupDTO = new OauthGroupDTO();
        oauthGroupDTO.setGroupId(authGroupInput.getGroupId());

        if (Objects.isNull(authGroupInput.getOffset())) {
            authGroupInput.setOffset(1);
        }
        if (Objects.isNull(authGroupInput.getLimit())) {
            authGroupInput.setLimit(20);
        }
        return oauthGroupController.selectUserByGroupId(oauthGroupDTO);
    }

    /**
     * @param oauthUserGroupDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个分组 -> 多个用户
     * @date 14:29 2020-04-26
     **/
    public Boolean saveOrUpdateUserGroup(OauthUserGroupDTO oauthUserGroupDTO) {
        return oauthGroupController.saveOrUpdateUserGroup(oauthUserGroupDTO);
    }

}
