package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/7/23 14:13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthTenantParamDTO implements Serializable {

    private static final long serialVersionUID = -456373296553229650L;
    /** 租户Id */
    private Long tenantId;

    /** key words */
    private String keyWords;

}
