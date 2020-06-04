package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/8/27 10:26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TenantLevelVO implements Serializable {

    private static final long serialVersionUID = 5108372372043140300L;

    /** id */
    private Long id;

    /** 容纳人数 */
    private Integer userCount;

    /** 租户级别 */
    private Integer tenantLevel;

    /** 开启状态 */
    private Boolean status;

    /** 描述 */
    private String description;

}
