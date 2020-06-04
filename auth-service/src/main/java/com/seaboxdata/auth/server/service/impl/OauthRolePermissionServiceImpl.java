package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seaboxdata.auth.server.dao.OauthRolePermissionMapper;
import com.seaboxdata.auth.server.model.OauthRolePermission;
import com.seaboxdata.auth.server.service.OauthRolePermissionService;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 角色-资源许可表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Service
@Slf4j
public class OauthRolePermissionServiceImpl implements OauthRolePermissionService {

    @Autowired
    private OauthRolePermissionMapper oauthRolePermissionMapper;

    /**
     * @param roleId
     * @return java.util.List<com.seaboxdata.auth.model.SysRolePermission>
     * @author makaiyu
     * @description 根据roleIds 获取permissionList
     * @date 18:23 2019/5/13
     **/
    @Override
    public List<OauthRolePermission> selectPermissionByRoleId(Long roleId) {
        if (Objects.isNull(roleId)) {
            log.info("selectPermissionByRoleId -> param is null");
            throw new ServiceException("400", "selectPermissionByRoleId -> param is null");
        }

        LambdaQueryWrapper<OauthRolePermission> wrapper = new QueryWrapper<OauthRolePermission>().lambda()
                .eq(OauthRolePermission::getRoleId, roleId);
        return MapperUtils.list(oauthRolePermissionMapper, wrapper);
    }
}
