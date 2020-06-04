package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author makaiyu
 * @date 2019/5/14 10:20
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthRoleDTO implements Serializable {

    private static final long serialVersionUID = 8978452674411577487L;

    /** 角色Id */
    private Long id;

    /** 权限Id */
    private List<Long> permissionsIds;

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
