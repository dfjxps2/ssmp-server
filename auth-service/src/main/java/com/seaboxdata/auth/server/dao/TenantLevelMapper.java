package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.TenantLevel;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 租户级别 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-08-27
 */
@Repository
public interface TenantLevelMapper extends BaseMapper<TenantLevel> {

    @Select("SELECT b.user_count FROM tenant_level b " +
            "LEFT JOIN oauth_tenant a on b.id=a.level_id " +
            "where a.id = #{tenantId} ;")
    Integer selectTenantLevelCount(Long tenantId);
}
