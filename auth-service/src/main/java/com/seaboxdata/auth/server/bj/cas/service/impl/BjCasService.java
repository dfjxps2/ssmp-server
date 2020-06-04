package com.seaboxdata.auth.server.bj.cas.service.impl;

import com.google.common.collect.Lists;
import com.seaboxdata.auth.server.bj.cas.model.CasOrganize;
import com.seaboxdata.auth.server.bj.cas.model.CasUser;
import com.seaboxdata.auth.server.bj.cas.service.ICasService;
import com.seaboxdata.auth.server.bj.cas.synchrodata.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class BjCasService implements ICasService {

    /**
     * 查询单个用户信息
     */
    @Override
    public CasUser casUser(String username) {
        return WebClient.casUser(username);
    }

    /**
     * 批量同步用户数据
     *
     * @return
     */
    @Override
    public List<CasUser> synchroUsers(String dateStart, String dataEnd) {
        return WebClient.casUsers(dateStart, dataEnd);
    }

    /**
     * 同步部门机构数据
     */
    @Override
    public List<CasOrganize> synchroOrgData() {

        // 所有需要同步数据
        List<CasOrganize> casOrganizes = WebClient.casOrganizes();

        // 所有存在子级的父级
        List<CasOrganize> reOrgs = Lists.newArrayList();

        // 承载父级与子级整合后 相关code
        List<String> orgCodes = Lists.newArrayList();

        //组合层级后的机构
        this.operationOrg(reOrgs, casOrganizes, "0", orgCodes);

        // 获取所有需要从原数据集中删除的数据
        List<CasOrganize> organizeList = casOrganizes.stream().filter(
                casOrganize -> orgCodes.contains(casOrganize.getOrgCode()))
                .collect(Collectors.toList());

        casOrganizes.removeAll(organizeList);
        casOrganizes.addAll(reOrgs);

        return casOrganizes;
    }

    /**
     * 将机构组合成层级关系
     */
    private void operationOrg(List<CasOrganize> reOrgs, List<CasOrganize> casOrganizes,
                              String parentCode, List<String> orgCodes) {
        for (CasOrganize casOrganize : casOrganizes) {
            if (parentCode.equals(casOrganize.getParentCode())) {
                reOrgs.add(casOrganize);
                orgCodes.add(casOrganize.getOrgCode());
                this.operationOrg(casOrganize.getChildOrgs(), casOrganizes, casOrganize.getOrgCode(), orgCodes);
            }
        }
    }
}
