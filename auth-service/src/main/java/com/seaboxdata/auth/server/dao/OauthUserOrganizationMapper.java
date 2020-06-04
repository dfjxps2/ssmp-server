package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthOrganization;
import com.seaboxdata.auth.server.model.OauthUserOrganization;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户-机构表 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-06-06
 */
@Repository
public interface OauthUserOrganizationMapper extends BaseMapper<OauthUserOrganization> {

    @Select("SELECT b.* FROM oauth_user_organization a left JOIN oauth_organization b " +
            "on a.organization_id = b.id where a.user_id = #{userId} and a.tenant_id = #{tenantId} and b.id is not null;")
    List<OauthOrganization> selectOrganizationUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
