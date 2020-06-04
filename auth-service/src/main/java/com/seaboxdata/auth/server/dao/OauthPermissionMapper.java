package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthPermission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 资源许可表 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Repository
public interface OauthPermissionMapper extends BaseMapper<OauthPermission> {

    @Select("select c.id , c.permission_code  from oauth_user_role a left join " +
            "oauth_role_permission b on a.role_id = b.role_id " +
            "left JOIN oauth_permission c on b.permission_id = c.id " +
            "where a.user_id = #{userId};")
    Set<OauthPermission> selectPermissionCodeByUserId(@Param("userId") Long userId);

    @Select({
            "<script>",
            "SELECT user.id FROM oauth_user as user" +
                    " LEFT JOIN oauth_user_role as user_role" +
                    " on user.id = user_role.user_id" +
                    " LEFT JOIN oauth_role_permission as role_permission" +
                    " on user_role.role_id = role_permission.role_id" +
                    " LEFT JOIN oauth_permission as permission" +
                    " on role_permission.permission_id = permission.id" +
                    " where user.tenant_id = #{tenantId} and permission.permission_code in " +
                    "<foreach collection='permissionCodes' item='id' open='(' separator=',' close=')'>" +
                    "#{id}" +
                    "</foreach>",
            "</script>"
    })
    List<Long> selectUserIdByPermissionCodes(@Param("permissionCodes") List<String> permissionCodes,
                                             @Param("tenantId") Long tenantId);

    @Select("select c.id , c.permission_code from oauth_user_role a left join " +
            "oauth_role_permission b on a.role_id = b.role_id " +
            "left JOIN oauth_permission c on b.permission_id = c.id " +
            "where a.user_id = #{userId} and c.app_name = #{appName} ;")
    Set<OauthPermission> selectPermissionsByUserIdAndSystem(@Param("userId") Long userId,
                                                            @Param("appName") String appName);

}
