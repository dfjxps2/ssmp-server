package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author makaiyu
 * @date 2019/5/31 11:21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthOrganizationVO implements Serializable {

    private static final long serialVersionUID = -2640299833151991943L;
    /** 机构Id */
    private Long organizationId;

    /** 父Id */
    private Long parentId;

    /** 机构层级等级 */
    private Integer level;

    /** 机构名称 */
    private String organizationName;

    /** 机构编码 */
    private String organizationCode;

    /** 机构编号 */
    private String organizationNumber;

    /** 机构地址 */
    private String organizationAddress;

    /** 当前登录人 */
    private String username;

    /** 负责人姓名 */
    private String managerName;

    /** 负责人用户Id */
    private Long managerUserId;

    /** 负责人电话 */
    private String managerPhone;

    /** 负责人邮箱 */
    private String managerMail;

    /** 该机构下的人员 */
    private Integer userCount;

    /** 部门下人员信息 */
    private List<OauthUserVO> oauthUsers;

}
