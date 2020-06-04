package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.dto.TenantLevelDTO;
import com.seaboxdata.auth.api.utils.BeanUtils;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.api.vo.TenantLevelVO;
import com.seaboxdata.auth.server.dao.OauthUserMapper;
import com.seaboxdata.auth.server.dao.TenantCodeMapper;
import com.seaboxdata.auth.server.dao.TenantLevelMapper;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.model.TenantCode;
import com.seaboxdata.auth.server.model.TenantLevel;
import com.seaboxdata.auth.server.service.TenantCodeService;
import com.seaboxdata.auth.server.service.TenantLevelService;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 租户级别 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-08-27
 */
@Service
public class TenantLevelServiceImpl extends
        ServiceImpl<TenantLevelMapper, TenantLevel> implements TenantLevelService {

    @Autowired
    private TenantLevelMapper tenantLevelMapper;

    @Autowired
    private TenantCodeMapper tenantCodeMapper;

    @Autowired
    private OauthUserMapper oauthUserMapper;

    @Autowired
    private TenantCodeService tenantCodeService;

    /**
     * @param tenantLevelDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存/修改租户级别
     * @date 10:45 2019/8/27
     **/
    @Override
    public Boolean saveOrUpdateTenantLevel(@RequestBody TenantLevelDTO tenantLevelDTO) {

        TenantLevel tenantLevel = new TenantLevel();
        BeanUtils.copyPropertiesIgnoreNull(tenantLevelDTO, tenantLevel);
        try {
            if (Objects.nonNull(tenantLevel.getId())) {
                tenantLevel.setUpdateTime(LocalDateTime.now());
                MapperUtils.updateById(tenantLevelMapper, tenantLevel);
            } else {
                MapperUtils.save(tenantLevelMapper, tenantLevel);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param tenantLevelIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 删除租户级别信息
     * @date 10:46 2019/8/27
     **/
    @Override
    public Boolean deleteTenantLevel(@RequestBody List<Long> tenantLevelIds) {

        // TODO 查询所有租户 是否存有此Id
//        LambdaQueryWrapper<OauthTenant> wrapper = new QueryWrapper<OauthTenant>().lambda()
//                .in(OauthTenant::getLevelId, tenantLevelIds);
//
//        List<OauthTenant> tenantList = MapperUtils.list(oauthTenantMapper, wrapper);
//        if (!CollectionUtils.isEmpty(tenantList)) {
//            throw new ServiceException("500", "此租户级别下仍存在租户");
//        }

        return MapperUtils.removeByIds(tenantLevelMapper, tenantLevelIds);
    }

    /**
     * @return java.util.List<com.seaboxdata.auth.api.vo.TenantLevelVO>
     * @author makaiyu
     * @description 查询全部用户级别信息
     * @date 10:46 2019/8/27
     **/
    @Override
    public List<TenantLevelVO> selectTenantLevel() {
        List<TenantLevel> levelList = MapperUtils.list(tenantLevelMapper, new QueryWrapper<TenantLevel>());

        List<TenantLevelVO> tenantLevels = Lists.newArrayListWithCapacity(levelList.size());
        levelList.forEach(tenantLevel -> {
            TenantLevelVO tenantLevelVO = new TenantLevelVO();
            BeanUtils.copyPropertiesIgnoreNull(tenantLevel, tenantLevelVO);
            tenantLevels.add(tenantLevelVO);
        });

        return tenantLevels;
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 检验当前租户是否满足创建用户的级别
     * @date 11:23 2019/8/27
     **/
    @Override
    public Boolean checkTenantLevel() {

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        TenantCode tenantCode = tenantCodeService.getTenantCodeByStatus(tenantId);

        if (Objects.isNull(tenantCode)) {
            throw new ServiceException("1400", "该租户尚未激活，请联系管租户理员");
        }

        // 获取租户级别
        Long tenantLevelId = tenantCode.getTenantLevelId();

        LambdaQueryWrapper<TenantLevel> tenantLevelWrapper = new QueryWrapper<TenantLevel>().lambda()
                .eq(TenantLevel::getId, tenantLevelId)
                .eq(TenantLevel::getStatus, true);

        TenantLevel tenantLevel = MapperUtils.getOne(tenantLevelMapper, tenantLevelWrapper, true);

        if (Objects.isNull(tenantLevel)) {
            throw new ServiceException("1400", "当前无此租户级别或此租户级别已关闭");
        }

        // 获取此租户级别下 可用用户数
        Integer userCount = tenantLevel.getUserCount();

        LambdaQueryWrapper<OauthUser> userWrapper = new QueryWrapper<OauthUser>().lambda()
                .eq(OauthUser::getTenantId, tenantId);

        // 获取该租户下 已建用户
        Integer getUserCount = MapperUtils.count(oauthUserMapper, userWrapper);

        if (userCount > getUserCount) {
            return true;
        } else {
            return false;
        }
    }
}
