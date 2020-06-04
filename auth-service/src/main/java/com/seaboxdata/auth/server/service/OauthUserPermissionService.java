package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.OauthUserPermissionDTO;
import com.seaboxdata.auth.server.model.OauthUserPermission;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 用户-资源许可表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
public interface OauthUserPermissionService {

    /**
     * @param userId
     * @return java.util.List<com.seaboxdata.auth.model.SysUserPermission>
     * @author makaiyu
     * @description 根据userId 获取用户资源数据
     * @date 15:54 2019/5/13
     **/
    List<OauthUserPermission> getUserPermissionByUserId(Long userId);

    /**
     * @param oauthUserPermission
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据权限码 增加用户权限
     * @date 16:59 2019/5/20
     **/
    Boolean saveOrUpdateUserPermission(@RequestBody OauthUserPermissionDTO oauthUserPermission);
}
