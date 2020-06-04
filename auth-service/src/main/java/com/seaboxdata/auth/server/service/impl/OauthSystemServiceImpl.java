package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.vo.OauthSystemVO;
import com.seaboxdata.auth.server.dao.OauthSystemMapper;
import com.seaboxdata.auth.server.model.OauthSystem;
import com.seaboxdata.auth.server.service.OauthSystemService;
import com.seaboxdata.commons.mybatis.MapperUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-30
 */
@Service
public class OauthSystemServiceImpl implements OauthSystemService {

    @Autowired
    private OauthSystemMapper oauthSystemMapper;

    @Override
    public List<OauthSystemVO> selectAllSystem(OauthSystemDTO oauthSystemDTO) {

        LambdaQueryWrapper<OauthSystem> lambda = new QueryWrapper<OauthSystem>().lambda();
        if (Objects.nonNull(oauthSystemDTO.getAppName())) {
            lambda.eq(OauthSystem::getAppName, oauthSystemDTO.getAppName());
        }

        List<OauthSystem> systems = MapperUtils.list(oauthSystemMapper, lambda);
        List<OauthSystemVO> oauthSystemVOS = Lists.newArrayListWithCapacity(systems.size());

        if (!CollectionUtils.isEmpty(systems)) {
            systems.forEach(system -> {
                OauthSystemVO systemVO = new OauthSystemVO();
                BeanUtils.copyProperties(system, systemVO);

                systemVO.setAppName(system.getAppName());
                oauthSystemVOS.add(systemVO);
            });
        }

        return oauthSystemVOS;
    }
}
