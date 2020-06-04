package com.seaboxdata.auth.api.dto;

import com.seaboxdata.commons.query.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author makaiyu
 * @date 2019/6/19 15:55
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OauthUserNamePageDTO extends PageDTO {

    private static final long serialVersionUID = 197550913979945492L;
    /**
     * 用户名
     */
    private String name;

    /**
     * 分组Id
     */
    private Long groupId;

    /**
     * 机构Id
     */
    private Long organizationId;
    /**
     * 员工等级Id
     */
    private Long staffLevelId;
}
