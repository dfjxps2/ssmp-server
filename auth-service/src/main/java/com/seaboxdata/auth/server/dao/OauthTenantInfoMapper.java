package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthTenantInfo;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 租户额外信息表 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-11-07
 */
@Repository
public interface OauthTenantInfoMapper extends BaseMapper<OauthTenantInfo> {

}
