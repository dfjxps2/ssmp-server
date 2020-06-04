package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seaboxdata.auth.api.dto.OauthTenantStatusDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.api.vo.TenantCodeVO;
import com.seaboxdata.auth.server.dao.OauthTenantMapper;
import com.seaboxdata.auth.server.dao.PlatformCodeMapper;
import com.seaboxdata.auth.server.dao.TenantCodeMapper;
import com.seaboxdata.auth.server.dao.TenantLevelMapper;
import com.seaboxdata.auth.server.model.OauthTenant;
import com.seaboxdata.auth.server.model.PlatformCode;
import com.seaboxdata.auth.server.model.TenantCode;
import com.seaboxdata.auth.server.model.TenantLevel;
import com.seaboxdata.auth.server.service.OauthTenantService;
import com.seaboxdata.auth.server.service.TenantCodeService;
import com.seaboxdata.auth.server.utils.AESUtils;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 租户-级别-激活码 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-09-03
 */
@Service
@Slf4j
public class TenantCodeServiceImpl
        extends ServiceImpl<TenantCodeMapper, TenantCode> implements TenantCodeService {

    @Autowired
    private TenantCodeMapper tenantCodeMapper;

    @Autowired
    private TenantLevelMapper tenantLevelMapper;

    @Autowired
    private OauthTenantMapper oauthTenantMapper;

    @Autowired
    private PlatformCodeMapper platformCodeMapper;

    @Autowired
    private OauthTenantService oauthTenantService;

    /**
     * @param activationCode
     * @return com.seaboxdata.auth.api.vo.TenantCodeVO
     * @author makaiyu
     * @description 校验激活码
     * @date 15:19 2019/9/3
     **/
    @Override
    public TenantCodeVO checkActivationCode(String activationCode) {

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        LambdaQueryWrapper<OauthTenant> tenantWrapper = new QueryWrapper<OauthTenant>().lambda()
                .eq(OauthTenant::getId, tenantId);

        // 获取租户信息
        OauthTenant tenant = MapperUtils.getOne(oauthTenantMapper, tenantWrapper, true);

        LambdaQueryWrapper<TenantCode> tenantCodeWrapper = new QueryWrapper<TenantCode>().lambda()
                .eq(TenantCode::getTenantId, tenantId)
                .orderByDesc(TenantCode::getCreateTime);

        // 获取租户-级别-激活码对象
        List<TenantCode> tenantCodes = MapperUtils.list(tenantCodeMapper, tenantCodeWrapper);

        if (CollectionUtils.isEmpty(tenantCodes)) {
            throw new ServiceException("1400", "创建激活码时产生错误！请联系管理员！");
        }

        TenantCode tenantCode = tenantCodes.get(0);

        // 获取当前租户级别
        Long tenantLevelId = tenantCode.getTenantLevelId();

        LambdaQueryWrapper<TenantLevel> tenantLevelWrapper = new QueryWrapper<TenantLevel>().lambda()
                .eq(TenantLevel::getId, tenantLevelId)
                .eq(TenantLevel::getStatus, true);

        TenantLevel tenantLevel = MapperUtils.getOne(tenantLevelMapper, tenantLevelWrapper, true);

        if (Objects.isNull(tenantLevel)) {
            throw new ServiceException("1402", "当前激活码无对应租户级别");
        }

        // 校验激活码
        Boolean flag = getCheckActivationCode(tenant.getId(), tenantLevelId, activationCode);
        if (!flag) {
            throw new ServiceException("1407", "校验失败，请联系管理员");
        }

        // 修改该租户TenantCode下状态
        LambdaUpdateWrapper<TenantCode> wrapper = new UpdateWrapper<TenantCode>().lambda()
                .eq(TenantCode::getTenantId, tenant.getId())
                .set(TenantCode::getStatus, false);

        MapperUtils.update(tenantCodeMapper, null, wrapper);

        // 修改校验完成后状态及激活码
        tenantCode.setStatus(true);
        tenantCode.setActivityCode(activationCode);
        tenantCode.setUpdateTime(LocalDateTime.now());

        MapperUtils.updateById(tenantCodeMapper, tenantCode);

        TenantCodeVO tenantCodeVO = new TenantCodeVO();
        tenantCodeVO.setTenantLevelName(tenantLevel.getDescription());
        tenantCodeVO.setTenantLevel(tenantLevel.getTenantLevel());

        return tenantCodeVO;
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验当前登录用户是否存在激活的激活码
     * @date 11:37 2019/9/5
     **/
    @Override
    public Boolean checkTenantCodeStatus() {

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        Long tenantId = userDetails.getTenantId();

        TenantCode tenantCode = getTenantCodeByStatus(tenantId);

        List<PlatformCode> codeList = MapperUtils.list(platformCodeMapper, new QueryWrapper<>());

        if (CollectionUtils.isEmpty(codeList)) {
            log.info("平台信息不存在！！");
            return false;
        }

        PlatformCode platformCode = codeList.get(0);

        // 生成激活码
        String code = AESUtils.generatorPlatformActivityCode(platformCode.getUserId().toString(),
                platformCode.getTenantUseCount().toString(), platformCode.getTimesTamp().toString());

        if (!code.equals(platformCode.getActivityCode())) {
            throw new ServiceException("1701", "当前平台信息发生变化，请联系管理员");
        }

        // 判断当前时间与平台过期时间
        Long millis = System.currentTimeMillis();

        if (platformCode.getTimesTamp() - millis > NumberUtils.LONG_ZERO) {
            if (Objects.isNull(tenantCode)) {
                return false;
            } else {
                // 校验激活码
                return getCheckActivationCode(tenantId, tenantCode.getTenantLevelId(),
                        tenantCode.getActivityCode());
            }
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
     * @param tenantId
     * @return com.seaboxdata.auth.server.model.TenantCode
     * @author makaiyu
     * @description 根据状态 获取租户-激活码数据
     * @date 14:37 2019/9/6
     **/
    @Override
    public TenantCode getTenantCodeByStatus(Long tenantId) {
        LambdaQueryWrapper<TenantCode> wrapper = new QueryWrapper<TenantCode>().lambda()
                .eq(TenantCode::getTenantId, tenantId)
                .eq(TenantCode::getStatus, true);

        return MapperUtils.getOne(tenantCodeMapper, wrapper, true);
    }

    /**
     * @param tenantId
     * @return java.lang.Integer
     * @author makaiyu
     * @description 获取当前登录用户的租户级别
     * @date 14:15 2019/9/10
     */
    @Override
    public Integer selectTenantLevelById(Long tenantId) {
        return tenantCodeMapper.selectTenantLevelById(tenantId);
    }

    /**
     * @return java.lang.String
     * @param, tenantId,  tenantLevelId     tenantUserDTO
     * @author makaiyu
     * @description 校验激活码
     * @date 13:31 2019/9/4
     **/
    private Boolean getCheckActivationCode(Long tenantId, Long tenantLevelId, String activationCode) {
        try {
            return AESUtils.checkActivationCode(tenantId.toString(), tenantLevelId.toString(), activationCode);
        } catch (Exception e) {
            log.warn("检验激活码失败！ 租户id:{} .传入激活码 : {}  ", tenantId, activationCode);
            throw new ServiceException("1700", "该激活码因被修改已无效，请联系管理员");
        }
    }

}
