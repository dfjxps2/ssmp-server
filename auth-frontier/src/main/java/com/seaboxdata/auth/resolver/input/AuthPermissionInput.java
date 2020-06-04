package com.seaboxdata.auth.resolver.input;

import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/6/19 18:34
 */
@Data
public class AuthPermissionInput {

    /** 权限名称 */
    private String permissionName;

    /** 权限码 */
    private String permissionCode;

    /** 父Id */
    private Long parentId;

    /** 资源详情 */
    private String description;

    /** 是否拥有该权限 */
    private Boolean flag;

    /** 系统枚举 */
    private AppKeyEnum appName;

}
