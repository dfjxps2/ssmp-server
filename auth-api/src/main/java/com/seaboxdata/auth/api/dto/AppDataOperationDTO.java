package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/7/26 9:43
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AppDataOperationDTO implements Serializable {
    private static final long serialVersionUID = -4155060120514888745L;

    /** 本次操作的dataTypeId */
    private Long sourceDataTypeId;

    /** 目标移动的dataTypeId*/
    private Long targetDataTypeId;

    /** true:上移 false:下移 */
    private Boolean moveOperation;

}
