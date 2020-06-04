package com.seaboxdata.auth.server.axis.service;

import com.seaboxdata.auth.server.axis.model.OrgInfo;
import com.seaboxdata.auth.server.axis.model.RoleInfo;
import com.seaboxdata.auth.server.axis.model.UserInfo;

public interface IAxisSynService {

    /**
     * 根据账号密码获取y用户的usercode
     * @param loginName
     * @param password
     * @return
     */
    String getUserCode(String loginName, String password);

    /**
     * 获取用户信息
     * @param userId
     * @param appId
     * @return
     *
     * 文档中getUserInfoByXml没有说明需要参数appId，但代码样例中提到，需要再确实中确认
     */
    String getUserInfo(String userId, String appId);

    String getData(String id, String method);

    /**
     * 获取机构
     * @param deptId
     * @return
     */
    String getDeptInfo(String deptId);

    /**
     * 获取角色
     * @param roleId
     * @return
     */
    String getRoleInfo(String roleId);

    /**
     * xml转UserInfo
     */
    UserInfo xmlToUserInfo(String xml);

    /**
     * xml转OrgInfo
     */
    OrgInfo xmlToOrgInfo(String xml);

    /**
     * xml转RoleInfo
     */
    RoleInfo xmlToRoleInfo(String xml);
}
