package com.seaboxdata.auth.api.dto;


import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/5/13 18:37
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthPermissionDTO implements Serializable {

    private static final long serialVersionUID = 3198674984124175949L;

    /** 资源Id */
    private Long permissionId;

    /** 权限名称 */
    private String permissionName;

    /** 权限码 */
    @ToString.Include
    private String permissionCode;

    /** 父Id */
    private Long parentId;

    /** 应用名称 */
    private AppKeyEnum appName;

    /** 资源详情 */
    private String description;

    /** 是否拥有该权限 */
    private Boolean flag;

}
