package com.seaboxdata.auth.resolver.input;

import lombok.Data;

import java.util.List;

/**
 * @author makaiyu
 * @date 2019/6/19 19:05
 */
@Data
public class AuthUserPermissionInput {

    /** 权限码Id */
    List<Long> permissionIds;

    /** 用户Id */
    private Long userId;

    /** 0:增 1:减 */
    private Long status;

    /** 操作人Id */
    private Long operatorId;

}
