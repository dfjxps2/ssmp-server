package com.seaboxdata.auth.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * (StaffLevel)表实体类
 *
 * @author makejava
 * @since 2020-03-30 11:20:30
 */
@Data
public class StaffLevelDTO implements Serializable {

    private static final long serialVersionUID = -7598617775889522717L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 等级名称
     */
    private String levelName;
    /**
     * 等级描述
     */
    private String levelDescription;
    /**
     * 工时核算
     */
    private Integer workingHour;
    /**
     * 对外报价
     */
    private Integer externalOffer;
    /**
     * 创建者
     */
    private Long creator;
    /**
     * 修改者
     */
    private Long modifier;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;


}
