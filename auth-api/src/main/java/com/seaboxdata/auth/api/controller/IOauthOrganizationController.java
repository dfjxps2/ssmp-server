package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.OauthOrganizationDTO;
import com.seaboxdata.auth.api.dto.OauthOrganizationParamDTO;
import com.seaboxdata.auth.api.vo.OauthOrganizationRedisVo;
import com.seaboxdata.auth.api.vo.OauthOrganizationVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
@FeignClient(contextId = "AuthOrganization",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthOrganizationController {

    /**
     * @param oauthOrganizationDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存机构信息
     * @date 10:48 2019/5/31
     **/
    @PostMapping("/organization/save")
    Boolean saveOauthOrganization(@RequestBody OauthOrganizationDTO oauthOrganizationDTO);


    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 获取全部机构及子机构信息
     * @date 13:00 2019/5/31
     **/
    @PostMapping("/organization/select/all")
    List<OauthOrganizationVO> selectAllOrganization(@RequestBody(required = false) OauthOrganizationParamDTO oauthOrganizationParamDTO);

    /**
     * @param organizationName
     * @return com.seaboxdata.auth.api.vo.OauthOrganizationVO
     * @author makaiyu
     * @description 根据OrganizationName 获取机构信息
     * @date 16:53 2020-03-05
     **/
    @GetMapping("/organization/select/name")
    OauthOrganizationVO selectOrganizationByName(@RequestParam("organizationName") String organizationName);

    /**
     * @param organizationIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除机构
     * @date 14:11 2019/5/31
     **/
    @DeleteMapping("/organization/delete")
    Boolean deleteOauthOrganization(@RequestBody List<Long> organizationIds);


    /**
     * @param organizationIds
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据机构Id查询机构信息
     * @date 14:49 2019/9/27
     **/
    @PostMapping("/organization/get/by/ids")
    List<OauthOrganizationVO> selectOrganizationByIds(@RequestBody List<Long> organizationIds);

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据用户Id获取机构信息
     * 若flag=true 则判断用户权限 若flag=false 则直接查询用户所属机构
     * @date 14:04 2020-01-14
     **/
    @PostMapping("/organization/select/user/id")
    List<OauthOrganizationVO> selectOrganizationByUserId(@RequestParam("flag") Boolean flag);

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthOrganizationVO>
     * @author makaiyu
     * @description 根据用户Id获取机构信息
     * @date 14:04 2020-01-14
     **/
    @PostMapping("/organization/list/select/user/id")
    List<OauthOrganizationVO> selectOrganizationListByUserId(@RequestParam("userId") Long userId);

    /**
     * 查询所有当前租户下的机构名
     *
     * @Author LiuZhanXi
     * @Date 2020/3/25 19:10
     **/
    @PostMapping("/organization/list/select/organizationName")
    List<OauthOrganizationRedisVo> selectAllOrganizationName();

}
