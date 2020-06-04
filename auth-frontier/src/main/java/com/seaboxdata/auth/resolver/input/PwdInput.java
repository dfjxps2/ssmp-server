package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/9/26 9:32
 */
@Data
public class PwdInput {

    /** 原始密码 */
    private String originalPwd;

    /** 新密码 */
    private String newPwd;

}
