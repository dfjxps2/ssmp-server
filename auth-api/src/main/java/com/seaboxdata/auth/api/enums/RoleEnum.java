package com.seaboxdata.auth.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author makaiyu
 * @date 2019/5/13 18:02
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    /** 管理员 */
    SYSMANAGER,
    /** 普通用户 */
    ORDINARYUSERS;

}
