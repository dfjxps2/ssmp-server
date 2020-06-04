package com.seaboxdata.auth.resolver.input;

import lombok.Data;

import java.util.List;

/**
 * @author makaiyu
 * @date 2019/7/25 10:56
 */
@Data
public class AuthSaveRoleInput {

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
