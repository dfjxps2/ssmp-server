package com.seaboxdata.auth.api.dto;

import com.seaboxdata.auth.api.enums.JumpEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/7/26 9:40
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AppDataParamDTO implements Serializable {

    private static final long serialVersionUID = 697311397605034953L;
    /** 主键id */
    private Long dataTypeId;

    /** 应用数据类型分类 */
    private String dataTypeName;

    /** 序号 */
    private Integer orderNumber;

    /** 应用数据类型url */
    private String url;

    /** level级别 */
    private Integer level;

    /** 跳转类型 */
    private JumpEnum jumpMode;

    /** 级别父Id */
    private Long parentId;

}
