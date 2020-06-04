package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/6/21 15:15
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthOrganizationParamDTO implements Serializable {

    private static final long serialVersionUID = 2370787628244132928L;
    /** 机构Id */
    private Long organizationId;

    /** key words  */
    private String keyWords;

}
