package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/11/7 14:13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TenantInfoDTO implements Serializable {

    private static final long serialVersionUID = -2109361747139148949L;

    /** tenantInfoId */
    private Long tenantInfoId;

    /** 虚拟货币：海贝值 */
    private Long virtualCurrency;

    /** 租户Id */
    private Long tenantId;

    /** 是否为drd资源负责租户*/
    private Boolean drdManager;

}
