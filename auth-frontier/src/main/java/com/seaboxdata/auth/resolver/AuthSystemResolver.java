package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IOauthSystemController;
import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.vo.OauthSystemVO;
import com.seaboxdata.auth.resolver.input.AuthSystemInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author makaiyu
 * @date 2019/7/25 14:52
 */
@Service
public class AuthSystemResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IOauthSystemController oauthSystemController;

    @Autowired
    public AuthSystemResolver(IOauthSystemController oauthSystemController) {
        this.oauthSystemController = oauthSystemController;
    }

    /**
     * @param authSystemInput
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthSystemVO>
     * @author makaiyu
     * @description 获取全部system (可模糊查)
     * @date 14:47 2019/7/25
     **/
    public List<OauthSystemVO> selectAllSystem(AuthSystemInput authSystemInput) {
        OauthSystemDTO oauthSystemDTO = new OauthSystemDTO();
        if (Objects.nonNull(authSystemInput)) {
            BeanUtils.copyProperties(authSystemInput, oauthSystemDTO);
        }
        return oauthSystemController.selectAllSystem(oauthSystemDTO);
    }

}
