package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * (SkillInfo)表实体类
 *
 * @author makejava
 * @since 2020-03-30 11:20:30
 */
@Data
@Accessors(chain = true)
public class SkillInfoDTO implements Serializable {

    private static final long serialVersionUID = -1273371578686644434L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 技能名称
     */
    private String skillName;
    /**
     * 技能等级
     */
    private String skillLevel;
    /**
     * 相关认证
     */
    private String relatedCertification;
    /**
     * 描述
     */
    private String description;
    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;
    /**
     * 记录修改时间
     */
    private LocalDateTime modifyTime;
    /**
     * 创建人
     */
    private Long creator;
    /**
     * 修改者
     */
    private Long modifier;


}