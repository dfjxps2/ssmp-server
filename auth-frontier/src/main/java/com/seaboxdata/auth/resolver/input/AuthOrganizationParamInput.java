package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/6/21 15:44
 */
@Data
public class AuthOrganizationParamInput {

    /** 机构Id */
    private Long organizationId;

    /** key words  */
    private String keyWords;

    /** 排序 true:正序 false:倒叙 */
    private Boolean order;

}
