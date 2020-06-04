package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/8/14 9:27
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AuthUserParamDTO implements Serializable {

    private static final long serialVersionUID = 817713297608119509L;

    /** key words*/
    private String keyWords;

    /** 租户ID */
    private Long tenantId;

}
