package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.OauthRoleDTO;
import com.seaboxdata.auth.api.dto.OauthUserRoleDTO;
import com.seaboxdata.auth.api.enums.RoleEnum;
import com.seaboxdata.auth.api.vo.OauthRoleVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@FeignClient(contextId = "AuthRole",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthRoleController {

    /**
     * @param userId
     * @return java.util.Set<com.seaboxdata.auth.model.SysRole>
     * @author makaiyu
     * @description 根据userId  获取 permissionCodes
     * @date 10:09 2019/5/14
     **/
    @GetMapping("/role/select/userid")
    List<OauthUserVO> selectUserByRoleId(@RequestParam("userId") Long userId);

    /**
     * @return java.util.List<OauthRole>
     * @author makaiyu
     * @description 获取所有角色列表
     * @date 9:59 2019/5/20
     **/
    @GetMapping("/role/select/all")
    List<OauthRoleVO> selectAllRole();

    /**
     * @param userId
     * @return java.util.Set<com.seaboxdata.auth.model.SysRole>
     * @author makaiyu
     * @description 根据userId  获取 permissionCodes
     * @date 10:09 2019/5/14
     **/
    @GetMapping("/role/get/permission/userid")
    Set<String> selectPermissionCodeByUserId(@RequestParam("userId") Long userId);

    /**
     * @param oauthRoleDTO
     * @return Boolean
     * @author makaiyu
     * @description 添加/修改角色信息 同时赋权限
     * @date 18:05 2019/5/27
     **/
    @PostMapping("/role/save/update")
    Boolean saveUpdateOauthRole(@RequestBody OauthRoleDTO oauthRoleDTO);

    /**
     * @param roleIds
     * @return Boolean
     * @author makaiyu
     * @description 根据roleIds 删除角色 同时删除中间表
     * @date 18:19 2019/5/27
     **/
    @DeleteMapping("/role/delete")
    Boolean deleteOauthRole(@RequestBody List<Long> roleIds);


    /**
     * @param userId, roleEnum
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断用户角色
     * @date 10:51 2019/8/19
     **/
    @PostMapping("/decide/user/role")
    Boolean decideUserRole(@RequestParam("userId") Long userId,
                           @RequestBody RoleEnum roleEnum);


    /**
     * @param userId, appName
     * @return java.util.Map<java.lang.Long, java.lang.String>
     * @author makaiyu
     * @description 根据用户Id、系统名称 查询其权限码及Id
     * @date 17:17 2019/11/25
     **/
    @GetMapping("/role/get/permission/userid/system")
    Map<Long, String> selectPermissionsByUserIdAndSystem(@RequestParam("userId") Long userId,
                                                         @RequestParam("appName") String appName);

    /**
     * @param oauthUserRoleDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存/修改用户角色信息
     * @date 14:05 2020-04-26
     **/
    @PostMapping("/role/user/set/more")
    Boolean saveOrUpdateUserRole(@RequestBody OauthUserRoleDTO oauthUserRoleDTO);

}
