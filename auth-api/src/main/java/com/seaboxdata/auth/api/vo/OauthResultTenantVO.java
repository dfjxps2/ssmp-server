package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/6/19 18:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OauthResultTenantVO implements Serializable {

    private static final long serialVersionUID = 7216859465100387316L;
    /** 租户ID */
    private Long tenantId;

    /** 租户名称 */
    private String tenantName;

    /** 租户编码 */
    private String tenantCode;

    /** 用户登录账号 */
    private String username;

    /** 租户级别-激活码Id */
    private Long tenantCodeId;

    /** 描述 */
    private String tenantDesc;

    /** 管理员密码 */
    private String password;

    /** 状态 0:启用 1:未启用 */
    private Integer status;

    /** 管理员姓名 */
    private String managerName;

    /** 管理员电话 */
    private String managerPhone;

    /** 管理员邮箱 */
    private String managerMail;

    /** 激活码 */
    private String activationCode;

    /** 租户级别 Id */
    private Long tenantLevelId;

    /** 租户详情表 */
    private TenantInfoVO tenantInfoVO;

    /** 激活码 */
    private String activityCode;

    /** 租户管理员状态: 1-可用，0-禁用 */
    private Boolean enabled;

}
