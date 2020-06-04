package com.seaboxdata.auth.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.seaboxdata.commons.utils.LocalDateTimeJsonDeserializer;
import com.seaboxdata.commons.utils.LocalDateTimeJsonSerializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.core.serializer.Serializer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author makaiyu
 * @date 2019/5/30 9:48
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthJxpmUserVO implements Serializable {

    private static final long serialVersionUID = -1091254051592103445L;
    /**
     * id
     */
    private Long userId;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 名称
     */
    private String name;

    /**
     * 主电话号码
     */
    private String phoneNumber;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 直属领导
     */
    private OauthUserVO directLeader;

    /**
     * 租户海贝值
     */
    private Long virtualCurrency;

    /**
     * 主电子邮箱
     */
    private String email;

    /**
     * 主固定电话
     */
    private String fixedPhoneNumber;

    /**
     * 最高学历
     */
    private String maxEducation;

    /**
     * 身份证号
     */
    private String identifyNum;

    /**
     * 星座
     */
    private String constellation;

    /**
     * 户籍地址
     */
    private String residenceAddress;
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
     * 外包公司名称
     */
    private String outsourcingCompany;

    /**
     * 外包公司电话
     */
    private String outsourcingPhone;

    /**
     * 角色Id
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 租户级别
     */
    private Integer tenantLevel;

    /**
     * 是否为系统用户
     */
    private Boolean systemUser;

    /**
     * 最后登录时间
     */
    @JsonSerialize(using = LocalDateTimeJsonSerializable.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime lastLoginTime;

    /**
     * 员工等级
     */
    private StaffLevelVo staffLevelVo;

    /**
     * 是否为租户管理员
     */
    private Boolean tenantManager;

    /**
     * 是否为系统管理员 大兴
     */
    private Boolean systemManager;

}
