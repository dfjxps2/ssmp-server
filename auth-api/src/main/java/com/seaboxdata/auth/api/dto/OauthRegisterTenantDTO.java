package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/5/28 15:21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthRegisterTenantDTO implements Serializable {

    private static final long serialVersionUID = 3615765625495753400L;
    /** 租户ID */
    private Long tenantId;

    /** 租户名称 */
    private String tenantName;

    /** 租户编码 */
    private String tenantCode;

    /** 描述 */
    private String tenantDesc;

    /** 登录账号 */
    private String username;

    /** 名称 */
    private String name;

    /** 密码 */
    private String password;

    /** 电话号码 */
    private String phoneNumber;

    /** 电子邮箱 */
    private String email;

    /** 租户级别Id */
    private Long tenantLevelId;

    /** 租户管理员状态: 1-可用，0-禁用 */
    private Boolean enabled;
}
