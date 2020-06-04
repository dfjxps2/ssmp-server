package com.seaboxdata.auth.server.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * (GraduationInfo)表实体类
 *
 * @author makejava
 * @since 2020-03-30 11:16:44
 */
@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
public class GraduationInfo {
    @TableId(type = IdType.INPUT)
    /**
     * 主键
     * */
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
    private LocalDateTime educationStartTime;
    /**
     * 毕业时间
     */
    private LocalDateTime graduateTime;
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
