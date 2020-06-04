package com.seaboxdata.auth.api.dto;

import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/7/25 14:19
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthRoleParamDTO implements Serializable {

    private static final long serialVersionUID = -905817985319302721L;
    /** 角色ID */
    private Long roleId;

    /** 应用名称 */
    private AppKeyEnum appName;

}
