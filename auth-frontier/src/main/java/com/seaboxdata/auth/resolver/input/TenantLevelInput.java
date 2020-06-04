package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/8/27 10:26
 */
@Data
public class TenantLevelInput {

    /** id */
    private Long id;

    /** 容纳人数 */
    private Integer userCount;

    /** 开启状态 */
    private Boolean status;

    /** 租户级别 */
    private Integer tenantLevel;

    /** 描述 */
    private String description;

}
