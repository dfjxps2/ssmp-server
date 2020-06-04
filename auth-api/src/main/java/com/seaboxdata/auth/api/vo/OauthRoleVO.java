package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/5/29 17:00
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthRoleVO implements Serializable {

    private static final long serialVersionUID = 7060777985620228929L;
    /** 主键id */
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色码 */
    private String roleCode;

    /** 可用状态 0：不可用  1：可用 */
    private Integer status;

    /** 角色父ID */
    private Long parentId;

    /** 租户Id */
    private Long tenantId;

    /** 角色描述 */
    private String description;


}
