package com.seaboxdata.auth.server.cas.service.impl;

import com.seaboxdata.auth.server.cas.model.CasOrganize;
import com.seaboxdata.auth.server.cas.model.CasUser;
import com.seaboxdata.auth.server.cas.service.ICasService;
import com.seaboxdata.auth.server.cas.ws.WSClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CasService implements ICasService {

    /**
     * 查询单个用户信息
     */
    @Override
    public CasUser casUser(String username){
        return WSClient.casUser(username);
    }

    /**
     * 批量同步用户数据
     * @return
     */
    @Override
    public List<CasUser> synchroUsers(String dateStart, String dataEnd){
        return WSClient.casUsers(dateStart, dataEnd);
    }

    /**
     * 同步部门机构数据
     */
    @Override
    public List<CasOrganize> synchroOrgData() {

        List<CasOrganize> casOrganizes = WSClient.casOrganizes();

        //组合层级后的机构
        List<CasOrganize> reOrgs = new ArrayList<>();
        this.operationOrg(reOrgs, casOrganizes, "0");

        return reOrgs;
    }

    /**
     * 将机构组合成层级关系
     */
    private void operationOrg(List<CasOrganize> reOrgs, List<CasOrganize> casOrganizes, String parentCode){
        for(CasOrganize casOrganize : casOrganizes){
            if(parentCode.equals(casOrganize.getParentCode())){
                reOrgs.add(casOrganize);
                this.operationOrg(casOrganize.getChildOrgs(), casOrganizes, casOrganize.getOrgCode());
            }
        }
    }
}
