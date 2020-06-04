package com.seaboxdata.auth.api.dto;

import com.seaboxdata.commons.query.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author makaiyu
 * @date 2019/5/31 15:09
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthGroupDTO extends PageDTO {

    private static final long serialVersionUID = 7439782248972044269L;
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
