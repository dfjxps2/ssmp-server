package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/9/3 13:45
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TenantCodeDTO implements Serializable {
    private static final long serialVersionUID = 3612449648829105895L;

    /** 激活码 */
    private String activationCode;


}
