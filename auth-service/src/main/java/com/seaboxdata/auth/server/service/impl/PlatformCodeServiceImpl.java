package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.seaboxdata.auth.api.dto.OauthTenantStatusDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.api.vo.PlatformCodeVO;
import com.seaboxdata.auth.server.dao.OauthTenantMapper;
import com.seaboxdata.auth.server.dao.PlatformCodeMapper;
import com.seaboxdata.auth.server.model.OauthTenant;
import com.seaboxdata.auth.server.model.PlatformCode;
import com.seaboxdata.auth.server.service.OauthTenantService;
import com.seaboxdata.auth.server.service.PlatformCodeService;
import com.seaboxdata.auth.server.utils.AESUtils;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 平台-激活码 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-09-06
 */
@Service
public class PlatformCodeServiceImpl implements PlatformCodeService {

    @Autowired
    private PlatformCodeMapper platformCodeMapper;

    @Autowired
    private OauthTenantService oauthTenantService;

    @Autowired
    private OauthTenantMapper oauthTenantMapper;

    /**
     * @param activityCode
     * @return java.lang.Long
     * @author makaiyu
     * @description 校验平台激活码
     * @date 11:25 2019/9/6
     **/
    @Override
    public PlatformCodeVO checkPlatformCode(String activityCode) {

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long userId = userDetails.getUserId();

        List<PlatformCode> platformCodes = getPlatformCodes(userId);

        if (CollectionUtils.isEmpty(platformCodes)) {
            throw new ServiceException("1400", "激活码生成失败，请联系管理员重新生成");
        }

        PlatformCode platformCode = platformCodes.get(0);

        // 若激活码正确 判断当前时间与过期时间
        Long millis = System.currentTimeMillis();

        if (platformCode.getTimesTamp() - millis < NumberUtils.LONG_ZERO) {
            throw new ServiceException("1700", "平台时间已过期，请重新激活！");
        }

        // 校验激活码
        Boolean flag = AESUtils.checkPlatformActivationCode(userId.toString()
                , platformCode.getTenantUseCount().toString()
                , platformCode.getTimesTamp(), activityCode);

        if (!flag) {
            throw new ServiceException("1400", "激活码验证失败!请联系管理员");
        }

        LambdaUpdateWrapper<PlatformCode> wrapper = new UpdateWrapper<PlatformCode>().lambda()
                .eq(PlatformCode::getUserId, userId)
                .orderByDesc(PlatformCode::getCreateTime)
                .set(PlatformCode::getStatus, true)
                .set(PlatformCode::getActivityCode, activityCode);

        MapperUtils.update(platformCodeMapper, platformCode, wrapper);

        // 将所有租户及用户置为启用
        setTenantStatus(true);

        LocalDateTime time = LocalDateTime.ofEpochSecond(
                platformCode.getTimesTamp() / 1000, 0, ZoneOffset.ofHours(8));

        PlatformCodeVO platformCodeVO = new PlatformCodeVO();
        platformCodeVO.setTenantUseCount(platformCode.getTenantUseCount())
                .setEndTime(time);
        return platformCodeVO;
    }

    /**
     * @param flag
     * @return void
     * @author makaiyu
     * @description 设置租户状态
     * @date 10:03 2019/9/9
     **/
    private void setTenantStatus(Boolean flag) {
        List<OauthTenant> tenantList = MapperUtils.list(oauthTenantMapper, new QueryWrapper<>());
        if (!CollectionUtils.isEmpty(tenantList)) {
            List<Long> tenantIds = tenantList.stream().map(OauthTenant::getId).collect(Collectors.toList());
            OauthTenantStatusDTO oauthTenantStatusDTO = new OauthTenantStatusDTO();
            oauthTenantStatusDTO.setIsEnable(flag)
                    .setTenantId(tenantIds);
            oauthTenantService.updateTenantStatus(oauthTenantStatusDTO);
        }
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断当前平台用户是否已激活
     * @date 11:39 2019/9/6
     **/
    @Override
    public Boolean checkPlatformStatus() {
        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long userId = userDetails.getUserId();

        // 获取当前登录平台用户信息
        List<PlatformCode> platformCodes = getPlatformCodes(userId);

        if (CollectionUtils.isEmpty(platformCodes)) {
            return false;
        }

        PlatformCode platformCode = platformCodes.get(0);

        if (!platformCode.getStatus()) {
            return false;
        }

        // 生成激活码
        String code = AESUtils.generatorPlatformActivityCode(userId.toString(),
                platformCode.getTenantUseCount().toString(), platformCode.getTimesTamp().toString());

        if (!code.equals(platformCode.getActivityCode())) {
            throw new ServiceException("1701", "当前平台信息发生变化，请联系管理员");
        }

        // 若激活码正确 判断当前时间与过期时间
        Long millis = System.currentTimeMillis();

        if (platformCode.getTimesTamp() - millis > NumberUtils.LONG_ZERO) {
            return true;
        } else {
            // 将所有租户及用户置为禁用
            setTenantStatus(false);
            // 将激活码设为失效
            platformCode.setStatus(false);
            MapperUtils.updateById(platformCodeMapper, platformCode);
            throw new ServiceException("1700", "平台时间已过期，请重新激活！");
        }
    }

    /**
     * @param userId
     * @return java.util.List<com.seaboxdata.auth.server.model.PlatformCode>
     * @author makaiyu
     * @description 获取当前登录平台用户信息
     * @date 13:42 2019/9/6
     */
    private List<PlatformCode> getPlatformCodes(Long userId) {
        LambdaQueryWrapper<PlatformCode> platformCodeWrapper = new QueryWrapper<PlatformCode>().lambda()
                .eq(PlatformCode::getUserId, userId)
                .orderByDesc(PlatformCode::getCreateTime);

        return MapperUtils.list(platformCodeMapper, platformCodeWrapper);
    }
}
