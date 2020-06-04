package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author makaiyu
 * @date 2019/7/23 15:49
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthTenantStatusDTO implements Serializable {

    private static final long serialVersionUID = 1957444005262716268L;
    /** 租户Id */
    private List<Long> tenantId;

    /** true ：启用  false：禁用*/
    private Boolean isEnable;

}
