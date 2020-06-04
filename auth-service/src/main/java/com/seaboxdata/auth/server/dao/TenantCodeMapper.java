package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.TenantCode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 租户-级别-激活码 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-09-03
 */
@Repository
public interface TenantCodeMapper extends BaseMapper<TenantCode> {

    @Select("SELECT tenant_level from tenant_level a LEFT JOIN tenant_code b on" +
            " a.id = b.tenant_level_id where b.`status`=1 and  b.tenant_id = #{tenantId}")
    Integer selectTenantLevelById(@Param("tenantId") Long tenantId);
}
