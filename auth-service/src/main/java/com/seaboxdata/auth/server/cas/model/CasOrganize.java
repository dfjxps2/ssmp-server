package com.seaboxdata.auth.server.cas.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CAS返回机构信息
 */
@Data
@Accessors(chain = true)
public class CasOrganize implements Serializable {

    /*
        <fieldinfo>
            <fieldname>SUP_DEP_GLOBAL_ID</fieldname>
            <fieldvale>350000C089</fieldvale>
        </fieldinfo>
        <fieldinfo>
            <fieldname>DEP_GLOBAL_ID</fieldname>
            <fieldvale>350000C089006</fieldvale>
        </fieldinfo>
        <fieldinfo>
            <fieldname>DEP_NAME</fieldname>
            <fieldvale>浦城县工业和信息化局</fieldvale>
        </fieldinfo>
     */

    /**
     * 父级code
     */
    private String parentCode;

    /**
     * 机构Code
     */
    private String orgCode;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 下属机构
     */
    private List<CasOrganize> childOrgs = new ArrayList<>();

    public static CasOrganize toModel(Map<String, Object> map){
        String parentCode = String.valueOf(map.get("SUP_DEP_GLOBAL_ID"));
        return new CasOrganize().setParentCode(StringUtils.isBlank(parentCode)? "0" : parentCode)
                .setOrgCode(String.valueOf(map.get("DEP_GLOBAL_ID")))
                .setOrgName(String.valueOf(map.get("DEP_NAME")));
    }
}
