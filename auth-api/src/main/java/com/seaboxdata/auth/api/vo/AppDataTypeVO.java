package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/7/26 9:34
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AppDataTypeVO implements Serializable {

    private static final long serialVersionUID = -2551850194397660308L;
    /** 主键id */
    private Long dataTypeId;

    /** 应用数据类型分类 */
    private String dataTypeName;

    /** 应用数据类型url */
    private String url;

    /** level级别 */
    private Integer level;

    /** 序号 */
    private Integer orderNumber;

    /** 跳转类型 */
    private String jumpMode;

    /** 级别父Id */
    private Long parentId;

}
