package com.seaboxdata.auth.resolver.input;

import com.seaboxdata.auth.api.enums.JumpEnum;
import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/7/26 11:25
 */
@Data
public class AppDataTypeParamInput {

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
