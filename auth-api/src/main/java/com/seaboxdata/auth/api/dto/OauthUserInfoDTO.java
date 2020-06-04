package com.seaboxdata.auth.api.dto;

import com.seaboxdata.auth.api.enums.ContactEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户信息-扩展表
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthUserInfoDTO implements Serializable {

    private static final long serialVersionUID = 7046093063879341549L;
    /** 联系类型 */
    private ContactEnum contact;

    /** 是否为主联系方式 */
    private Boolean isPrimary;

    /** 联系信息 */
    private String information;

    /** 联系方式描述 */
    private String infoDesc;

    /** 用户ID */
    private Long userId;

}
