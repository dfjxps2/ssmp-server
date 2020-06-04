package com.seaboxdata.auth.server.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * (StaffLevel)表实体类
 *
 * @author makejava
 * @since 2020-03-30 11:20:30
 */
@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
public class StaffLevel {
    @TableId(type = IdType.INPUT)
    /**主键*/
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
