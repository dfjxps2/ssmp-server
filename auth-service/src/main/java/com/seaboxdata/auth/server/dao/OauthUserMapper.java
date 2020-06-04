package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Repository
public interface OauthUserMapper extends BaseMapper<OauthUser> {

}
