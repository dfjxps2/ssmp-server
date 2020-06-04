package com.seaboxdata.auth.api.dto;

import com.seaboxdata.auth.api.vo.OauthGroupVO;
import com.seaboxdata.auth.api.vo.OauthRoleVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author makaiyu
 * @date 2019/5/13 18:02
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthUserDTO implements Serializable {

    private static final long serialVersionUID = 74335887421213094L;
    /**
     * 主键id
     */
    private Long id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 名称
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 用户联系方式
     */
    private List<OauthUserInfoDTO> oauthUserInfos;

    /**
     * 毕业相关信息
     */
    private List<GraduationInfoDTO> graduationInfos;

    /**
     * 技能信息
     */
    private List<SkillInfoDTO> skillInfos;

    /**
     * 身份证号
     */
    private String identifyNum;

    /**
     * 最高学历
     */
    private String maxEducation;

    /**
     * 星座
     */
    private String constellation;

    /**
     * 员工等级
     */
    private StaffLevelDTO staffLevelDTO;

    /**
     * 户籍地址
     */
    private String residenceAddress;

    /**
     * 附件：存放名片
     */
    private String userAttachment;

    /**
     * 工号
     */
    private String jobNumber;

    /**
     * 状态: 1-可用，0-禁用
     */
    private Boolean enabled;

    /**
     * 用户性别
     */
    private Integer userSex;

    /**
     * 用户生日
     */
    private String userBirthday;

    /**
     * 用户职位
     */
    private String position;

    /**
     * 用户职称
     */
    private String title;

    /**
     * 个人介绍
     */
    private String personalSignature;

    /**
     * 返回用户角色信息
     */
    private List<OauthRoleVO> roles;

    /**
     * 分组信息
     */
    private List<OauthGroupVO> groups;

}
