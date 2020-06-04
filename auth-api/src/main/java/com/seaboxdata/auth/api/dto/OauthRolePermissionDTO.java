package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/5/29 15:22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthRolePermissionDTO implements Serializable {

    private static final long serialVersionUID = -6998542704726444793L;
    /** 角色Id */
    @ToString.Include
    private Long roleId;

    /** 资源许可Id */
    private Long permissionId;

}
