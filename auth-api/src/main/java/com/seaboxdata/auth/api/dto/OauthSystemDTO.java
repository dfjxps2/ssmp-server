package com.seaboxdata.auth.api.dto;

import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/7/25 14:59
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthSystemDTO implements Serializable {

    private static final long serialVersionUID = 1109730225043810536L;
    /** 应用名称 */
    private AppKeyEnum appName;

}
