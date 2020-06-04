package com.seaboxdata.auth.server.axis.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrgInfo implements Serializable {

    /**
     * 组织标识
     */
    private String orgCode;

    /**
     * 组织名称
     */
    private String name;

    /**
     * ca组织标示
     */
    private String caCode;

    /**
     * 上一级组织标识
     */
    private String parentCode;

    /**
     * 组织排序号
     */
    private Integer orgOrder;
}
