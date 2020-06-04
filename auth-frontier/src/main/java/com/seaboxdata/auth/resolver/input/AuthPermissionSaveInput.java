package com.seaboxdata.auth.resolver.input;

import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.Data;

import java.util.List;

/**
 * @author makaiyu
 * @date 2019/6/19 18:29
 */
@Data
public class AuthPermissionSaveInput {

    /** 所添加权限Id */
    private List<Long> permissionIds;

    /** 角色Id */
    private Long roleId;

    /** 应用名称 */
    private AppKeyEnum appName;

}
