package com.seaboxdata.auth.server.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.seaboxdata.commons.utils.LocalDateTimeJsonDeserializer;
import com.seaboxdata.commons.utils.LocalDateTimeJsonSerializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
public class OauthUser implements Serializable {

    private static final long serialVersionUID = 4321996875508474346L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ToString.Include
    private Long id;

    /**
     * 登录账号
     */
    @ToString.Include
    private String username;

    /**
     * 名称
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 主移动电话
     */
    private String phoneNumber;

    /**
     * 直属领导
     */
    private Long directLeader;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 身份证号
     */
    private String identifyNum;

    /**
     * 主电子邮箱
     */
    private String email;

    /**
     * 主固定电话
     */
    private String fixedPhoneNumber;

    /**
     * 主传真号码
     */
    private String faxNumber;

    /**
     * 工号
     */
    private String jobNumber;

    /**
     * 状态: 1-可用，0-禁用
     */
    private Boolean enabled;

    /**
     * 用户性别
     */
    private Integer userSex;

    /**
     * 用户生日
     */
    private String userBirthday;

    /**
     * 最高学历
     */
    private String maxEducation;

    /**
     * 星座
     */
    private String constellation;

    /**
     * 员工等级id
     */
    private Long staffLevelId;

    /**
     * 户籍地址
     */
    private String residenceAddress;

    /**
     * 外包公司名称
     */
    private String outsourcingCompany;

    /**
     * 外包公司电话
     */
    private String outsourcingPhone;

    /**
     * 用户职位
     */
    private String position;

    /**
     * 用户职称
     */
    private String title;

    /**
     * 个人介绍
     */
    private String personalSignature;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 附件：存放名片
     */
    private String userAttachment;

    /**
     * 用户地址
     */
    private String userAddress;

    /**
     * 是否为系统用户
     */
    private Boolean systemUser;

    /**
     * 是否为租户管理员
     */
    private Boolean tenantManager;

    /**
     * 是否为系统管理员 大兴
     */
    private Boolean systemManager;

    /**
     * 最后登录时间
     */
    @JsonSerialize(using = LocalDateTimeJsonSerializable.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = LocalDateTimeJsonSerializable.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonSerialize(using = LocalDateTimeJsonSerializable.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime updateTime;

}
