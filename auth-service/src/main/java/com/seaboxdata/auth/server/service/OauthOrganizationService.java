package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.OauthOrganizationDTO;
import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.vo.OauthOrganizationRedisVo;
import com.seaboxdata.auth.api.vo.OauthOrganizationVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
public interface OauthOrganizationService {

    /**
     * @param oauthOrganizationDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存机构信息
     * @date 10:48 2019/5/31
     **/
    Boolean saveOauthOrganization(@RequestBody OauthOrganizationDTO oauthOrganizationDTO,
                                  @RequestParam("userId") Long userId,
                                  @RequestParam("tenantId") Long tenantId);

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 获取全部机构及子机构信息
     * @date 13:00 2019/5/31
     **/
    List<OauthOrganizationVO> selectAllOrganization(@RequestBody OauthOrganizationParamDTO oauthOrganizationParamDTO);


    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 获取全部机构及子机构信息
     * @date 13:00 2019/5/31
     **/
    List<OauthOrganizationVO> selectAllOrganizationAndUesrInfo(@RequestBody OauthOrganizationParamDTO oauthOrganizationParamDTO);

    /**
     * @param organizationIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除机构
     * @date 14:11 2019/5/31
     **/
    Boolean deleteOauthOrganization(@RequestBody List<Long> organizationIds);

    /**
     * @param organizationIds
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据机构Id查询机构信息
     * @date 14:49 2019/9/27
     **/
    List<OauthOrganizationVO> selectOrganizationByIds(@RequestBody List<Long> organizationIds);

    /**
     * @param organizationCodes
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据code码删除机构
     * @date 15:14 2019-12-09
     **/
    Boolean deleteOauthOrganizationByCodes(@RequestBody List<String> organizationCodes);

    /**
     * @return java.util.List<java.lang.String>
     * @author makaiyu
     * @description 获取全部机构码
     * @date 15:51 2020-01-06
     **/
    List<String> selectAllOrganizationCodes();


    /**
     * @param userId
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据用户Id获取机构信息
     * @date 14:04 2020-01-14
     */
    List<OauthOrganizationVO> selectOrganizationByUserId(@RequestParam("userId") Long userId);

    /**
     * @param organizationName
     * @return com.seaboxdata.auth.api.vo.OauthOrganizationVO
     * @author makaiyu
     * @description 根据OrganizationName 获取机构信息
     * @date 16:53 2020-03-05
     **/
    OauthOrganizationVO selectOrganizationByName(String organizationName);

    /**
     * 获取全部机构名
     *
     * @Author LiuZhanXi
     * @Date 2020/3/25 18:57
     **/
    List<OauthOrganizationRedisVo> selectAllOrganizationName();

}
