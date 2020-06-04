package com.seaboxdata.auth.resolver.input;

import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/6/19 18:26
 */
@Data
public class AuthRoleIdInput {

    /** 角色ID */
    private Long roleId;

    /** 应用名称 */
    private AppKeyEnum appName;

}
