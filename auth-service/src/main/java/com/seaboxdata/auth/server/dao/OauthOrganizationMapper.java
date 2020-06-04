package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthOrganization;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
@Repository
public interface OauthOrganizationMapper extends BaseMapper<OauthOrganization> {

    @Select("SELECT id FROM oauth_organization where parent_id = #{organizationId} and tenant_id = #{tenantId}")
    Set<Long> selectOrganizationByParentId(@Param("organizationId") Long organizationId,
                                           @Param("tenantId") Long tenantId);

    @Select("SELECT id FROM oauth_organization where parent_id = #{organizationId}")
    Set<Long> selectOrganizationByParent(@Param("organizationId") Long organizationId);

    @Select({
            "<script>",
            "SELECT id FROM oauth_organization where parent_id in" +
                    "<foreach collection='oauthOrganizationIds' item='id' open='(' separator=',' close=')'>" +
                    "#{id}" +
                    "</foreach>",
            "</script>"
    })
    Set<Long> selectOrganizationByParentIds(@Param("oauthOrganizationIds") Set<Long> oauthOrganizationIds,
                                            @Param("tenantId") Long tenantId);

    @Select({
            "<script>",
            "SELECT id FROM oauth_organization where parent_id in" +
                    "<foreach collection='oauthOrganizationIds' item='id' open='(' separator=',' close=')'>" +
                    "#{id}" +
                    "</foreach>",
            "</script>"
    })
    Set<Long> selectOrganizationByParents(@Param("oauthOrganizationIds") Set<Long> oauthOrganizationIds);
}
