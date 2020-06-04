package com.seaboxdata.auth.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * (GraduationInfo)表实体类
 *
 * @author makejava
 * @since 2020-03-30 11:16:44
 */
@Data
public class GraduationInfoDTO implements Serializable {

    private static final long serialVersionUID = -8046617495957152878L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 毕业院校名称
     */
    private String graduationSchool;
    /**
     * 学历
     */
    private String education;
    /**
     * 描述
     */
    private String description;
    /**
     * 入学时间
     */
    private String educationStartTime;
    /**
     * 毕业时间
     */
    private String graduateTime;
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
