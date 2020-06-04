package com.seaboxdata.auth.server.bj.cas.service;

import com.seaboxdata.auth.server.model.OauthUser;

public interface ISynchDataService {

    /**
     * @param username
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 查询本地auth没有username的数据，可以实时查询user的数据后做同步
     * @date 18:33 2019/10/17
     **/
    Boolean checkLoginByCasUsername(String username);

    /**
     * @param adminUser
     * @return void
     * @author makaiyu
     * @description 机构不存在时进行机构数据同步
     * @date 18:33 2019/10/17
     **/
    void synchOrgnize(OauthUser adminUser);

    /**
     * @param adminUser
     * @return void
     * @author makaiyu
     * @description 同步用户信息
     * @date 18:33 2019/10/17
     **/
    void synchUsers(OauthUser adminUser);

    /**
     * @return void
     * @author makaiyu
     * @description 同步数据
     * @date 18:34 2019/10/17
     **/
    void synchData();
}
