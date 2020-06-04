package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/6/19 18:14
 */
@Data
public class AuthOrganizationInput {

    /** 机构Id */
    private Long organizationId;

    /** 机构父Id */
    private Long parentId;

    /** 机构层级 */
    private Integer level;

    /** 机构名称 */
    private String organizationName;

    /** 机构编号 */
    private Integer organizationNumber;

    /** 机构地址 */
    private String organizationAddress;

    /** 负责人姓名 */
    private String managerName;

    /** 机构编码 */
    private String organizationCode;

    /** 负责人电话 */
    private String managerPhone;

    /** 负责人邮箱 */
    private String managerMail;

}
