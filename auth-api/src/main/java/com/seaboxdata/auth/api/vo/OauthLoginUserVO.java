package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/6/24 17:30
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthLoginUserVO implements Serializable {

    private static final long serialVersionUID = -4515607203769538521L;
    /** 用户Id */
    private Long userId;

    /** 租户Id */
    private Long tenantId;

}
