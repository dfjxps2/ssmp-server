package com.seaboxdata.auth.resolver.input;

import com.seaboxdata.commons.query.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author makaiyu
 * @date 2019/6/19 16:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthGroupInput extends PageDTO {

    private static final long serialVersionUID = 8425620029233616253L;
    /** 分组Id */
    private Long groupId;

    /** 查询条件 */
    private String keyWords;

}
