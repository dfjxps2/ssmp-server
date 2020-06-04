package com.seaboxdata.auth.server.axis.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RoleInfo implements Serializable {

    /**
     * 角色标识
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色所在组织标识
     */
    private String orgCode;

}
