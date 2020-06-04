package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author makaiyu
 * @date 2019/6/6 9:56
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthLoginResultVO implements Serializable {

    private static final long serialVersionUID = 3416004827900976487L;
    /** 用户Id */
    private Long userId;

    /** 租户Id */
    private Long tenantId;

    /** 用户名称 */
    private String name;

    /** 登陆账号 */
    private String username;

    /** 用户联系方式 */
    private List<OauthUserInfoVO> userInfoVOS;
}
