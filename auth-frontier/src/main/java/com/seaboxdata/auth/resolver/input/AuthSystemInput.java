package com.seaboxdata.auth.resolver.input;

import com.seaboxdata.commons.enums.AppKeyEnum;
import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/7/25 14:54
 */
@Data
public class AuthSystemInput {

    /** 应用名称 */
    private AppKeyEnum appName;
}
