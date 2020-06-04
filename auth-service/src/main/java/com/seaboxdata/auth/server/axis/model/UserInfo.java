package com.seaboxdata.auth.server.axis.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserInfo implements Serializable {

    /**
     * 用户标识
     */
    private String userCode;

    /**
     * CA用户标示
     */
    private String caCode;

    /**
     * 用户名
     */
    private String name;

    /**
     * 登录名
     */
    private String userLoginName;

    /**
     * 密码
     */
    private String synpassword;

    /**
     * 用户性别0：男；1：女（应用系统根据需要同步）
     */
    private String sex;

    /**
     * 地址
     */
    private String address;

    /**
     * 身份证号
     */
    private String identityCard;

    /**
     * 邮政编码
     */
    private String postCode;

    /**
     * 办公室电话
     */
    private String officePhone;

    /**
     * 移动电话
     */
    private String mobilePhone;

    /**
     * 传真
     */
    private String fax;

    /**
     * 电子邮件
     */
    private String email;

    /**
     *  用户类型
     *  0:个人用户；1：机构用户
     */
    private String userType;

    /**
     * 用户岗位
     */
    private String userPost;

    /**
     * 用户职务
     */
    private String userDuty;

    /**
     * 用户所属机构标识
     */
    private String orgCode;

    /**
     * 用户排序号
     */
    private String userOrder;

    /**
     * 角色列表
     */
    private List<String> roleList;
}
