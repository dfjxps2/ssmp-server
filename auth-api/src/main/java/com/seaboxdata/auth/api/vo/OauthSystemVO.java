package com.seaboxdata.auth.api.vo;

import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/7/25 14:43
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthSystemVO implements Serializable {

    private static final long serialVersionUID = -5197375434446909376L;
    /** 应用名称 */
    private AppKeyEnum appName;

    /** 系统名称 */
    private String systemName;

    /** 系统描述 */
    private String systemDesc;

}
