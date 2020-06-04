package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthOrganizationController;
import com.seaboxdata.auth.api.dto.OauthOrganizationDTO;
import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.api.vo.OauthOrganizationRedisVo;
import com.seaboxdata.auth.api.vo.OauthOrganizationVO;
import com.seaboxdata.auth.server.service.OauthOrganizationService;
import com.seaboxdata.auth.server.service.OauthPermissionService;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.jxpm.api.controller.IPmProductController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
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
public class OauthOrganizationController implements IOauthOrganizationController {

    @Autowired
    private OauthOrganizationService oauthOrganizationService;

    @Autowired
    private OauthPermissionService oauthPermissionService;

    private static final String NRD_AUTH = "NRD_AUTH_CODE";

    /**
     * @param oauthOrganizationDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存机构信息
     * @date 10:48 2019/5/31
     **/
    @Override
    public Boolean saveOauthOrganization(@RequestBody OauthOrganizationDTO oauthOrganizationDTO) {
        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        return oauthOrganizationService.saveOauthOrganization(oauthOrganizationDTO,
                userDetails.getUserId(), userDetails.getTenantId());
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 获取全部机构及子机构信息
     * @date 13:00 2019/5/31
     **/
    @Override
    public List<OauthOrganizationVO> selectAllOrganization(@RequestBody(required = false) OauthOrganizationParamDTO oauthOrganizationParamDTO) {
        return oauthOrganizationService.selectAllOrganization(oauthOrganizationParamDTO);
    }

    /**
     * @param organizationName
     * @return com.seaboxdata.auth.api.vo.OauthOrganizationVO
     * @author makaiyu
     * @description 根据OrganizationName 获取机构信息
     * @date 16:53 2020-03-05
     **/
    @Override
    public OauthOrganizationVO selectOrganizationByName(String organizationName) {
        return oauthOrganizationService.selectOrganizationByName(organizationName);
    }

    /**
     * @param organizationIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除机构
     * @date 14:11 2019/5/31
     **/
    @Override
    public Boolean deleteOauthOrganization(@RequestBody List<Long> organizationIds) {
        return oauthOrganizationService.deleteOauthOrganization(organizationIds);
    }

    /**
     * @param organizationIds
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据机构Id查询机构信息
     * @date 14:49 2019/9/27
     **/
    @Override
    public List<OauthOrganizationVO> selectOrganizationByIds(@RequestBody List<Long> organizationIds) {
        return oauthOrganizationService.selectOrganizationByIds(organizationIds);
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据用户Id获取机构信息
     * 若flag=true 则判断用户权限 若flag=false 则直接查询用户所属机构
     * @date 14:04 2020-01-14
     **/
    @Override
    public List<OauthOrganizationVO> selectOrganizationByUserId(Boolean flag) {
        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        if (flag) {
            List<Long> userIds = oauthPermissionService.selectUserIdByPermissionCodes(
                    Collections.singletonList(NRD_AUTH), userDetails.getTenantId());

            if (userIds.contains(UserUtils.getUserDetails().getUserId())) {
                return oauthOrganizationService.selectAllOrganizationAndUesrInfo(new OauthOrganizationParamDTO());
            }
        }
        return oauthOrganizationService.selectOrganizationByUserId(userDetails.getUserId());
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据用户Id获取机构信息
     * @date 14:04 2020-01-14
     **/
    @Override
    public List<OauthOrganizationVO> selectOrganizationListByUserId(Long userId) {
        return oauthOrganizationService.selectOrganizationByUserId(userId);
    }

    /**
     * 根据租户id查询所有机构名
     *
     * @Author LiuZhanXi
     * @Date 2020/3/25 19:14
     **/
    @Override
    public List<OauthOrganizationRedisVo> selectAllOrganizationName() {
        return oauthOrganizationService.selectAllOrganizationName();
    }

}
