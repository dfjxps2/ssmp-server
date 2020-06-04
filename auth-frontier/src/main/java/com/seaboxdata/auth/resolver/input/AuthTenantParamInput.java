package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/7/23 14:10
 */
@Data
public class AuthTenantParamInput {

    /** 租户Id */
    private Long tenantId;

    /** key words */
    private String keyWords;

}
