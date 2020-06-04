package com.seaboxdata.auth.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seaboxdata.auth.api.dto.OauthClientDetailsDTO;
import com.seaboxdata.auth.api.utils.BeanUtils;
import com.seaboxdata.auth.server.dao.OauthClientDetailsMapper;
import com.seaboxdata.auth.server.model.OauthClientDetails;
import com.seaboxdata.auth.server.service.OauthClientDetailsService;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.mybatis.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-16
 */
@Service
@Slf4j
public class OauthClientDetailsServiceImpl implements OauthClientDetailsService {

    @Autowired
    private OauthClientDetailsMapper oauthClientDetailsMapper;

    /**
     * @param clientId
     * @return OauthClientDetails
     * @author makaiyu
     * @description 根据code 获取oauth对象
     * @date 18:02 2019/5/16
     **/
    @Override
    public OauthClientDetailsDTO selectOauthClientByClientId(String clientId) {

        if (StringUtils.isEmpty(clientId)) {
            throw new ServiceException("400", "selectOauthClientByClientId param is null");
        }

        log.info("selectOauthClientByClientId -> clientId : {} ", clientId);
        LambdaQueryWrapper<OauthClientDetails> wrapper = new QueryWrapper<OauthClientDetails>().lambda()
                .eq(OauthClientDetails::getClientId, clientId);
        OauthClientDetails details = MapperUtils.getOne(oauthClientDetailsMapper, wrapper, true);

        OauthClientDetailsDTO dto = new OauthClientDetailsDTO();
        BeanUtils.copyPropertiesIgnoreNull(details, dto);

        return dto;
    }
}
