package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/8/14 9:24
 */
@Data
public class AuthUserParamInput {

    private String keyWords;

    private Long tenantId;

}
