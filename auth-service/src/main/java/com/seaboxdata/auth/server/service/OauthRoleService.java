package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.OauthRoleDTO;
import com.seaboxdata.auth.api.dto.OauthUserRoleDTO;
import com.seaboxdata.auth.api.enums.RoleEnum;
import com.seaboxdata.auth.server.model.OauthRole;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
public interface OauthRoleService {

    /**
     * @param userId
     * @return java.util.Set<com.seaboxdata.auth.model.SysRole>
     * @author makaiyu
     * @description 根据userId  获取 permissionCodes
     * @date 10:09 2019/5/14
     **/
    Set<String> selectPermissionCodeByUserId(@RequestParam("userId") Long userId);

    /**
     * @return java.util.List<OauthRole>
     * @author makaiyu
     * @description 获取所有角色列表
     * @date 9:59 2019/5/20
     **/
    List<OauthRole> selectAllRole();


    /**
     * @param userId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据用户Id获取所有角色Id
     * @date 10:46 2020-04-02
     **/
    List<Long> selectAllRoleByUserId(@RequestParam("userId") Long userId);


    /**
     * @param oauthRoleDTO
     * @return Boolean
     * @author makaiyu
     * @description 添加角色 同时赋权限
     * @date 18:05 2019/5/27
     **/
    Boolean saveUpdateOauthRole(@RequestBody OauthRoleDTO oauthRoleDTO);

    /**
     * @param roleIds
     * @return Boolean
     * @author makaiyu
     * @description 根据roleIds 删除角色 同时删除中间表
     * @date 18:19 2019/5/27
     **/
    Boolean deleteOauthRole(@RequestBody List<Long> roleIds);

    /**
     * @param userId, roleEnum
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断用户角色
     * @date 10:51 2019/8/19
     **/
    Boolean decideUserRole(@RequestParam("userId") Long userId, @RequestBody RoleEnum roleEnum);

    /**
     * @param userId, appName
     * @return java.util.Map<java.lang.Long, java.lang.String>
     * @author makaiyu
     * @description 根据用户Id、系统名称 查询其权限码及Id
     * @date 17:17 2019/11/25
     **/
    Map<Long, String> selectPermissionsByUserIdAndSystem(Long userId, String appName);

    /**
     * @param oauthUserRoleDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存/修改用户角色信息
     * @date 14:05 2020-04-26
     **/
    Boolean saveOrUpdateUserRole(@RequestBody OauthUserRoleDTO oauthUserRoleDTO);

}
