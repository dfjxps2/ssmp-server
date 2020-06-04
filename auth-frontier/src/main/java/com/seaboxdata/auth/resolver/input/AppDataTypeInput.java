package com.seaboxdata.auth.resolver.input;

import lombok.Data;

/**
 * @author makaiyu
 * @date 2019/7/26 11:25
 */
@Data
public class AppDataTypeInput {

    /** date type id*/
    private Long dataTypeId;

    /** 关键字搜索 */
    private String keyWords;


}
