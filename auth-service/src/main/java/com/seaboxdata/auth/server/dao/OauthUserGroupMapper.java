package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthGroup;
import com.seaboxdata.auth.server.model.OauthUserGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-31
 */
@Repository
public interface OauthUserGroupMapper extends BaseMapper<OauthUserGroup> {

    @Select("SELECT b.* FROM oauth_user_group a left JOIN oauth_group b " +
            "on a.group_id = b.id where a.user_id = #{userId} and a.tenant_id = #{tenantId};")
    List<OauthGroup> selectGroupByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
