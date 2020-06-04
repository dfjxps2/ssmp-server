package com.seaboxdata.auth.server.service.impl;

import com.seaboxdata.auth.api.dto.OauthLogUserDTO;
import com.seaboxdata.auth.api.utils.BeanUtils;
import com.seaboxdata.auth.api.utils.UserUtils;
import com.seaboxdata.auth.api.utils.domain.Message;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.auth.server.dao.OauthLogUserMapper;
import com.seaboxdata.auth.server.model.OauthLogUser;
import com.seaboxdata.auth.server.service.OauthLogUserService;
import com.seaboxdata.commons.mybatis.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 用户登陆日志表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-08-09
 */
@Service
public class OauthLogUserServiceImpl implements OauthLogUserService {

    @Autowired
    private OauthLogUserMapper oauthLogUserMapper;

    /**
     * @param message
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 发送用户登录消息
     * @date 15:50 2019/8/9
     **/
    @Override
    public Boolean sendLoginMessage(@RequestBody Message message) {
        OauthLoginUserVO userDetails = UserUtils.getUserDetails();
        OauthLogUser oauthLogUser = new OauthLogUser();
//        oauthLogUser.set
        return null;
    }

    /**
     * @param oauthLogUserDTO
     * @return boolean
     * @author makaiyu
     * @description 保存记录
     * @date 9:33 2019/8/16
     **/
    @Override
    public boolean saveLogUser(@RequestBody OauthLogUserDTO oauthLogUserDTO) {

        OauthLogUser oauthLogUser = new OauthLogUser();
        BeanUtils.copyPropertiesIgnoreNull(oauthLogUserDTO, oauthLogUser);
        oauthLogUser.setLoginUserId(oauthLogUser.getLoginUserId());
        return MapperUtils.save(oauthLogUserMapper, oauthLogUser);
    }
}
