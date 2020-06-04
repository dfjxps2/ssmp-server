package com.seaboxdata.auth.server.controller;

import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.controller.IOauthUserPermissionController;
import com.seaboxdata.auth.api.dto.OauthUserPermissionDTO;
import com.seaboxdata.auth.server.model.OauthUserPermission;
import com.seaboxdata.auth.server.service.OauthUserPermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 用户-资源许可表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Service
public class OauthUserPermissionController implements IOauthUserPermissionController {

    @Autowired
    private OauthUserPermissionService oauthUserPermissionService;

    /**
     * @param userId
     * @return java.util.List<com.seaboxdata.auth.model.SysUserPermission>
     * @author makaiyu
     * @description 根据userId 获取用户资源数据
     * @date 15:54 2019/5/13
     **/
    @Override
    public List<OauthUserPermissionDTO> getUserPermissionByUserId(Long userId) {
        List<OauthUserPermission> userPermissions = oauthUserPermissionService.getUserPermissionByUserId(userId);
        List<OauthUserPermissionDTO> userPermissionDTOS = Lists.newArrayListWithCapacity(userPermissions.size());
        if (!CollectionUtils.isEmpty(userPermissions)) {
            userPermissions.forEach(userPermission -> {
                OauthUserPermissionDTO dto = new OauthUserPermissionDTO();
                BeanUtils.copyProperties(userId, dto);
                userPermissionDTOS.add(dto);
            });
        }
        return userPermissionDTOS;
    }

    /**
     * @param oauthUserPermission
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据权限码 增加用户权限
     * @date 16:59 2019/5/20
     **/
    @Override
    public Boolean saveOrUpdateUserPermission(@RequestBody OauthUserPermissionDTO oauthUserPermission) {
        return oauthUserPermissionService.saveOrUpdateUserPermission(oauthUserPermission);
    }
}
