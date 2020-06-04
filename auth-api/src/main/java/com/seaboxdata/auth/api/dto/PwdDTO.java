package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/9/26 9:07
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PwdDTO implements Serializable {
    private static final long serialVersionUID = 7892271801705318215L;

    /** 原始密码 */
    private String originalPwd;

    /** 新密码 */
    private String newPwd;

}
