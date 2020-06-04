package com.seaboxdata.auth.server.cas.service;

import com.seaboxdata.auth.server.cas.model.CasOrganize;
import com.seaboxdata.auth.server.cas.model.CasUser;

import java.util.List;

public interface ICasService {

    /**
     * 查询单个用户信息
     */
    CasUser casUser(String username);

    /**
     * 批量同步用户数据
     * @return
     */
    List<CasUser> synchroUsers(String dateStart, String dataEnd);

    /**
     * 同步部门机构数据
     */
    List<CasOrganize> synchroOrgData();
}
