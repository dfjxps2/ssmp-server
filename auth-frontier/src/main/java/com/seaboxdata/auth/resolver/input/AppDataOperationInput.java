package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/7/26 9:43
 */
@Data
public class AppDataOperationInput {

    /** 本次操作的dataTypeId */
    private Long sourceDataTypeId;

    /** 目标移动的dataTypeId*/
    private Long targetDataTypeId;

    /** true:上移 false:下移 */
    private Boolean moveOperation;

}
