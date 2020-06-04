package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/9/3 13:45
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TenantCodeVO implements Serializable {
    private static final long serialVersionUID = 3612449648829105895L;

    /** 当前租户级别 */
    private String tenantLevelName;

    /** 租户级别 */
    private Integer tenantLevel;

}
