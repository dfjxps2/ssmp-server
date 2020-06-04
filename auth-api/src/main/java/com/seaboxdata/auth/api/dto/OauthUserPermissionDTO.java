package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author makaiyu
 * @date 2019/5/17 18:33
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthUserPermissionDTO implements Serializable {

    private static final long serialVersionUID = 535177885328225942L;
    /** 权限码Id */
    List<Long> permissionIds;

    /** 用户Id */
    private Long userId;

    /** 0:增 1:减 */
    private Long status;

    /** 操作人Id */
    private Long operatorId;

}
