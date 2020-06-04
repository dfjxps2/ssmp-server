package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/7/25 11:18
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthGroupParamDTO implements Serializable {
    private static final long serialVersionUID = -6351556813617697108L;

    /** 分组Id */
    private Long groupId;

    /** 查询条件 */
    private String keyWords;

}
