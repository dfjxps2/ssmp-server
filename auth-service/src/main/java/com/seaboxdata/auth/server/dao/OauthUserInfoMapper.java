package com.seaboxdata.auth.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seaboxdata.auth.server.model.OauthUserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户信息-扩展表 Mapper 接口
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
@Repository
public interface OauthUserInfoMapper extends BaseMapper<OauthUserInfo> {

    @Select("SELECT * FROM oauth_user_info where user_id = #{userId};")
    List<OauthUserInfo> selectUserInfoByUserId(@Param("userId") Long userId);
}
