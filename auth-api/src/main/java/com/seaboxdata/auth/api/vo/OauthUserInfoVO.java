package com.seaboxdata.auth.api.vo;

import com.seaboxdata.auth.api.enums.ContactEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/6/17 14:59
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OauthUserInfoVO implements Serializable {

    private static final long serialVersionUID = 9143843676555446720L;
    /** 联系类型 */
    private ContactEnum contact;

    /** 是否为主联系方式 */
    private Boolean isPrimary;

    /** 联系方式描述 */
    private String infoDesc;

    /** 联系信息 */
    private String information;

}
