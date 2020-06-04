package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/5/31 15:17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthGroupVO implements Serializable {

    private static final long serialVersionUID = -838254960308526016L;
    /** 分组Id */
    private Long groupId;

    /** 分组名称 */
    private String groupName;

    /** 工作描述 */
    private String groupDesc;

    /** 登录用户名 */
    private String username;

    /** 负责人姓名 */
    private String managerName;

    /** 负责人电话 */
    private String managerPhone;

    /** 负责人邮箱 */
    private String managerMail;

}
