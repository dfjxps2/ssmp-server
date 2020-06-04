package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthUserPermission;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户-资源许可表 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Repository
public interface OauthUserPermissionMapper extends BaseMapper<OauthUserPermission> {

}
