package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seaboxdata.auth.api.dto.OauthUserPermissionDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.server.dao.OauthPermissionMapper;
import com.seaboxdata.auth.server.dao.OauthUserPermissionMapper;
import com.seaboxdata.auth.server.model.OauthPermission;
import com.seaboxdata.auth.server.model.OauthUserPermission;
import com.seaboxdata.auth.server.service.OauthUserPermissionService;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户-资源许可表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Service
@Slf4j
public class OauthUserPermissionServiceImpl implements OauthUserPermissionService {

    @Autowired
    private OauthUserPermissionMapper oauthUserPermissionMapper;

    @Autowired
    private OauthPermissionMapper oauthPermissionMapper;

    /**
     * @param userId
     * @return java.util.List<com.seaboxdata.auth.model.SysUserPermission>
     * @author makaiyu
     * @description 根据userId 获取用户资源数据
     * @date 15:54 2019/5/13
     **/
    @Override
    public List<OauthUserPermission> getUserPermissionByUserId(Long userId) {
        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();
        LambdaQueryWrapper<OauthUserPermission> wrapper = new QueryWrapper<OauthUserPermission>().lambda()
                .eq(OauthUserPermission::getUserId, userId)
                .eq(OauthUserPermission::getTenantId, tenantId);
        return MapperUtils.list(oauthUserPermissionMapper, wrapper);
    }

    /**
     * @param oauthUserPermission
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据权限码 增加用户权限
     * @date 16:59 2019/5/20
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateUserPermission(@RequestBody OauthUserPermissionDTO oauthUserPermission) {

        if (Objects.isNull(oauthUserPermission) || CollectionUtils.isEmpty(oauthUserPermission.getPermissionIds())) {
            throw new ServiceException("400", "param is null");
        }

        // 获取传入permissionIds
        List<Long> permissionIds = oauthUserPermission.getPermissionIds();
        LambdaQueryWrapper<OauthPermission> wrapper = new QueryWrapper<OauthPermission>().lambda()
                .in(OauthPermission::getId, permissionIds);

        List<OauthPermission> permissionList = MapperUtils.list(oauthPermissionMapper, wrapper);
        Map<Long, String> permissionMap = permissionList.stream().collect(Collectors.toMap(OauthPermission::getId, OauthPermission::getPermissionCode));
        Long userId = oauthUserPermission.getUserId();
        try {
            if (!CollectionUtils.isEmpty(permissionMap)) {
                permissionMap.forEach((permissionId, permissionCode) -> {
                    OauthUserPermission permission = new OauthUserPermission();
                    MapperUtils.save(oauthUserPermissionMapper, permission.setPermissionId(permissionId)
                            .setPermissionCode(permissionCode)
                            .setUserId(userId)
                            .setStatus(oauthUserPermission.getStatus())
                            .setOperatorId(oauthUserPermission.getOperatorId()));
                });
            }
        } catch (Exception e) {
            log.warn("error:{}", e.getMessage());
            throw new ServiceException("500", "单独增加用户权限时出错");
        }

        return true;
    }
}
