package com.seaboxdata.auth.resolver.input;

import lombok.Data;

import java.util.List;

/**
 * @author makaiyu
 * @date 2019/7/23 15:44
 */
@Data
public class AuthTenantStatusInput {

    /** 租户Id */
    private List<Long> tenantId;

    /** true ：启用  false：禁用*/
    private Boolean isEnable;

}
