package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/6/19 18:07
 */
@Data
public class AuthGroupSaveInput {

    /** 分组Id */
    private Long groupId;

    /** 分组名称 */
    private String groupName;

    /** 分组描述 */
    private String groupDesc;

    /** 负责人姓名 */
    private String managerName;

    /** 负责人电话 */
    private String managerPhone;

    /** 负责人邮箱 */
    private String managerMail;

}
