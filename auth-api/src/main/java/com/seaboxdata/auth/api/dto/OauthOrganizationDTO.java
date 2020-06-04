package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/5/31 10:42
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthOrganizationDTO implements Serializable {

    private static final long serialVersionUID = 6956468165101703146L;
    /** 机构Id */
    private Long organizationId;

    /** 机构父Id */
    private Long parentId;

    /** 机构名称 */
    private String organizationName;

    /** 机构编号 */
    private Integer organizationNumber;

    /** 机构层级 */
    private Integer level;

    /** 机构编码 */
    private String organizationCode;

    /** 机构地址 */
    private String organizationAddress;

    /** 负责人姓名 */
    private String managerName;

    /** 负责人电话 */
    private String managerPhone;

    /** 负责人邮箱 */
    private String managerMail;

    /** 租户Id 为drd提供*/
    private Long tenantId;

}
