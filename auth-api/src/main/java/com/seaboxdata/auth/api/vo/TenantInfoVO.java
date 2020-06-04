package com.seaboxdata.auth.api.vo;

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
public class TenantInfoVO implements Serializable {

    private static final long serialVersionUID = -2109361747139148949L;

    /** Id */
    private Long tenantInfoId;

    /** 虚拟货币：海贝值 */
    private Long virtualCurrency;

    /** 租户ID */
    private Long tenantId;

    /** 记录创建者 */
    private Long creator;

    /** 是否为drd资源负责租户*/
    private Boolean drdManager;

}
