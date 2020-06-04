package com.seaboxdata.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2020-01-02 14:04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1887763344878185563L;

    /** 国家 */
    private String country;

    /** 城市 */
    private String city;

    /** IP */
    private String ip;

    /** id */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 租户ID */
    private Long tenantId;


}
