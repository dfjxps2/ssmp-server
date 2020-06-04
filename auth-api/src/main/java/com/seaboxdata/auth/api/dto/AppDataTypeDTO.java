package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/7/26 9:35
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AppDataTypeDTO implements Serializable {

    private static final long serialVersionUID = 6238878057476524566L;

    /** date type id*/
    private Long dataTypeId;

    /** 关键字搜索 */
    private String keyWords;

}
