package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthTenant;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 租户信息表 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-21
 */
@Repository
public interface OauthTenantMapper extends BaseMapper<OauthTenant> {

}
