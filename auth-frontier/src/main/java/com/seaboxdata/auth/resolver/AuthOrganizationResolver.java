package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IOauthOrganizationController;
import com.seaboxdata.auth.api.dto.OauthOrganizationDTO;
import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.vo.OauthOrganizationRedisVo;
import com.seaboxdata.auth.api.vo.OauthOrganizationVO;
import com.seaboxdata.auth.resolver.input.AuthOrganizationInput;
import com.seaboxdata.auth.resolver.input.AuthOrganizationParamInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author makaiyu
 * @date 2019/5/31 14:27
 */
@Service
public class AuthOrganizationResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IOauthOrganizationController oauthOrganizationController;

    @Autowired
    public AuthOrganizationResolver(IOauthOrganizationController oauthOrganizationController) {
        this.oauthOrganizationController = oauthOrganizationController;
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 获取全部机构及子机构信息
     * @date 13:00 2019/5/31
     **/
    public List<OauthOrganizationVO> selectAllOrganization(AuthOrganizationParamInput authOrganizationInput) {
        OauthOrganizationParamDTO oauthOrganizationParamDTO = new OauthOrganizationParamDTO();
        if (Objects.nonNull(authOrganizationInput)) {
            BeanUtils.copyProperties(authOrganizationInput, oauthOrganizationParamDTO);
            if (Objects.nonNull(authOrganizationInput.getOrder()) && !authOrganizationInput.getOrder()) {
                return oauthOrganizationController.selectAllOrganization(oauthOrganizationParamDTO).stream()
                        .sorted(Comparator.comparing(OauthOrganizationVO::getOrganizationId).reversed()).collect(Collectors.toList());
            }
        }
        return oauthOrganizationController.selectAllOrganization(oauthOrganizationParamDTO).stream()
                .sorted(Comparator.comparing(OauthOrganizationVO::getOrganizationId)).collect(Collectors.toList());
    }


    /**
     * @param authOrganizationInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存机构信息
     * @date 10:48 2019/5/31
     **/
    @PreAuthorize("hasAuthority('ucManager')")
    public Boolean saveOrUpdateOauthOrganization(AuthOrganizationInput authOrganizationInput) {

        OauthOrganizationDTO oauthOrganizationDTO = new OauthOrganizationDTO();
        BeanUtils.copyProperties(authOrganizationInput, oauthOrganizationDTO);

        return oauthOrganizationController.saveOauthOrganization(oauthOrganizationDTO);
    }

    /**
     * @param organizationIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除机构
     * @date 14:11 2019/5/31
     **/
    @PreAuthorize("hasAuthority('ucManager')")
    public Boolean deleteOauthOrganization(List<Long> organizationIds) {
        return oauthOrganizationController.deleteOauthOrganization(organizationIds);
    }

    /**
     * @param flag
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据用户Id获取机构信息
     * 若flag=true 则判断用户权限 若flag=false 则直接查询用户所属机构
     * @date 14:04 2020-01-14
     **/
    public List<OauthOrganizationVO> selectOrganizationByUserId(Boolean flag) {
        return oauthOrganizationController.selectOrganizationByUserId(flag);
    }

    /**
     * 根据租户查询全部机构名
     *
     * @Author LiuZhanXi
     * @Date 2020/3/25 19:21
     **/
    public List<OauthOrganizationRedisVo> selectAllOrganizationName() {
        return oauthOrganizationController.selectAllOrganizationName().stream()
                .sorted(Comparator.comparing(OauthOrganizationRedisVo::getId)).collect(Collectors.toList());
    }
}
