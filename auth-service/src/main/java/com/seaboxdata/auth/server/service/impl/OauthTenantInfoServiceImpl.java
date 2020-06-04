package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.dto.TenantInfoDTO;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.api.vo.TenantInfoVO;
import com.seaboxdata.auth.server.dao.OauthTenantInfoMapper;
import com.seaboxdata.auth.server.model.OauthTenantInfo;
import com.seaboxdata.auth.server.service.OauthTenantInfoService;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 租户额外信息表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-11-07
 */
@Service
@Slf4j
public class OauthTenantInfoServiceImpl implements OauthTenantInfoService {

    @Autowired
    private OauthTenantInfoMapper oauthTenantInfoMapper;

    /**
     * @param tenantInfoDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 保存租户信息
     * @date 14:14 2019/11/7
     **/
    @Override
    public Boolean saveOrUpdateTenantInfo(@RequestBody List<TenantInfoDTO> tenantInfoDTOs) {

        if (CollectionUtils.isEmpty(tenantInfoDTOs)) {
            log.info("save tenant info param is null");
            return false;
        }

        OauthLoginUserVO userDetails = UserUtils.getUserDetails();

        LambdaQueryWrapper<OauthTenantInfo> queryWrapper = new QueryWrapper<OauthTenantInfo>().lambda()
                .eq(OauthTenantInfo::getDrdManager, true);

        MapperUtils.remove(oauthTenantInfoMapper, queryWrapper);

        tenantInfoDTOs.forEach(tenantInfoDTO -> {
            Long tenantId;
            if (Objects.isNull(tenantInfoDTO.getTenantId())) {
                tenantId = userDetails.getTenantId();
            } else {
                tenantId = tenantInfoDTO.getTenantId();
            }

            LambdaQueryWrapper<OauthTenantInfo> wrapper = new QueryWrapper<OauthTenantInfo>().lambda()
                    .eq(OauthTenantInfo::getTenantId, tenantId);

            OauthTenantInfo info = MapperUtils.getOne(oauthTenantInfoMapper, wrapper, true);

            OauthTenantInfo tenantInfo = new OauthTenantInfo();

            BeanUtils.copyProperties(tenantInfoDTO, tenantInfo);
            tenantInfo.setTenantId(tenantId)
                    .setCreator(userDetails.getUserId())
                    .setUpdateTime(LocalDateTime.now());

            if (Objects.isNull(info)) {
                MapperUtils.save(oauthTenantInfoMapper, tenantInfo);
            } else {
                tenantInfo.setId(info.getId());
                if (Objects.nonNull(tenantInfoDTO.getVirtualCurrency())) {
                    tenantInfo.setVirtualCurrency(info.getVirtualCurrency() + tenantInfoDTO.getVirtualCurrency());
                }
                MapperUtils.updateById(oauthTenantInfoMapper, tenantInfo);
            }
        });

        return true;
    }

    /**
     * @param tenantInfoDTO
     * @return com.seaboxdata.auth.api.vo.TenantInfoVO
     * @author makaiyu
     * @description 根据租户Id  查询租户信息
     * @date 14:16 2019/11/7
     **/
    @Override
    public List<TenantInfoVO> selectTenantInfo(TenantInfoDTO tenantInfoDTO) {

        if (Objects.isNull(tenantInfoDTO)) {
            return Lists.newArrayList();
        }

        LambdaQueryWrapper<OauthTenantInfo> wrapper = new QueryWrapper<OauthTenantInfo>().lambda();

        if (Objects.nonNull(tenantInfoDTO.getDrdManager())) {
            wrapper.eq(OauthTenantInfo::getDrdManager, tenantInfoDTO.getDrdManager());
        }

        if (Objects.nonNull(tenantInfoDTO.getTenantId())) {
            wrapper.eq(OauthTenantInfo::getTenantId, tenantInfoDTO.getTenantId());
        }

        List<OauthTenantInfo> tenantInfos = MapperUtils.list(oauthTenantInfoMapper, wrapper);

        if (CollectionUtils.isEmpty(tenantInfos)) {
            log.info("tenantInfos is null ; selectTenantInfoByTenantId param :{}", tenantInfoDTO);
            return Lists.newArrayList();
        }

        List<TenantInfoVO> tenantInfoVOs = Lists.newArrayListWithCapacity(tenantInfos.size());

        tenantInfos.forEach(tenantInfo -> {
            TenantInfoVO tenantInfoVO = new TenantInfoVO();
            BeanUtils.copyProperties(tenantInfo, tenantInfoVO);
            tenantInfoVO.setTenantInfoId(tenantInfo.getId());
            tenantInfoVOs.add(tenantInfoVO);
        });

        return tenantInfoVOs;
    }

    /**
     * @param tenantIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据租户Ids 删除租户信息
     * @date 14:19 2019/11/7
     **/
    @Override
    public Boolean deleteTenantInfoByTenantId(@RequestBody List<Long> tenantIds) {

        if (CollectionUtils.isEmpty(tenantIds)) {
            return false;
        }

        LambdaQueryWrapper<OauthTenantInfo> wrapper = new QueryWrapper<OauthTenantInfo>().lambda()
                .in(OauthTenantInfo::getTenantId, tenantIds);

        return MapperUtils.remove(oauthTenantInfoMapper, wrapper);

    }
}
