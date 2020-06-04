package com.seaboxdata.auth.resolver.input;

import com.seaboxdata.commons.query.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author makaiyu
 * @date 2019/6/19 18:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthUserNamePageInput extends PageDTO {

    /**
     * 用户名
     */
    private String name;

    /**
     * 分组Id
     */
    private Long groupId;

    /**
     * 机构Id
     */
    private Long organizationId;

    /**
     * 员工等级Id
     */
    private Long staffLevelId;

}
