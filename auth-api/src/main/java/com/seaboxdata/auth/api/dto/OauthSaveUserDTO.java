package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author makaiyu
 * @date 2019/5/30 13:25
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthSaveUserDTO implements Serializable {

    private static final long serialVersionUID = 661898815702358759L;
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
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户联系方式
     */
    private List<OauthUserInfoDTO> oauthUserInfos;

    /**
     * 直属领导
     */
    private Long directLeader;

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
     * 外包公司名称
     */
    private String outsourcingCompany;

    /**
     * 外包公司电话
     */
    private String outsourcingPhone;

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
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户地址
     */
    private String userAddress;

    /**
     * 传入角色Ids
     */
    private List<Long> roleIds;

    /**
     * 传入分组Ids
     */
    private List<Long> groupIds;

    /**
     * 传入机构Ids
     */
    private List<Long> organizationIds;

    /**
     * 是否为系统管理员 大兴
     */
    private Boolean systemManager;

}
