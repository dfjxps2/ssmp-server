package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthLogUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户登陆日志表 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-08-09
 */
@Repository
public interface OauthLogUserMapper extends BaseMapper<OauthLogUser> {

}
