package com.seaboxdata.auth.api.vo;

import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author makaiyu
 * @date 2019/6/3 18:14
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthPermissionVO {

    /** 资源Id */
    private Long permissionId;

    /** 权限名称 */
    private String permissionName;

    /** 权限码 */
    @ToString.Include
    private String permissionCode;

    /** 应用名称 */
    private AppKeyEnum appName;

    /** 父Id */
    private Long parentId;

    /** 资源详情 */
    private String description;

    /** 是否拥有该权限 */
    private Boolean flag;

}
