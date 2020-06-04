package com.seaboxdata.auth.server.cas.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * CAS返回用户信息
 */
@Data
@Accessors(chain = true)
public class CasUser implements Serializable {

    /*
        <fieldinfo>
            <fieldname>USER_NAME</fieldname>
            <fieldvale>ca100102</fieldvale>
        </fieldinfo>
        <fieldinfo>
            <fieldname>USER_REAL_NAME</fieldname>
            <fieldvale>李四</fieldvale>
        </fieldinfo>
        <fieldinfo>
            <fieldname>USER_STATE</fieldname>
            <fieldvale>1</fieldvale>
        </fieldinfo>
        <fieldinfo>
            <fieldname>USER_ADDR</fieldname>
            <fieldvale>福建浦城县五一三路200号</fieldvale>
        </fieldinfo>
        <fieldinfo>
            <fieldname>USER_TEL</fieldname>
            <fieldvale>13433001111</fieldvale>
        </fieldinfo>
     */

    /**
     * 用户登录账号
     */
    private String username;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 用户状态：0未激活，1已激活，2冻结
     */
    private String status;

    /**
     * 用户地址
     */
    private String userAddr;

    /**
     * 用户联系电话
     */
    private String userTel;

    /**
     * 归属机构
     */
    private CasOrganize casOrganize;

    public static CasUser toModel(Map<String, Object> map){
        return new CasUser().setUsername(String.valueOf(map.get("USER_NAME")))
                .setRealName(String.valueOf(map.get("USER_REAL_NAME")))
                .setStatus(String.valueOf(map.get("USER_STATE")))
                .setUserAddr(String.valueOf(map.get("USER_ADDR")))
                .setUserTel(String.valueOf(map.get("USER_TEL")));
    }
}
