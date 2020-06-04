package com.seaboxdata.auth.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author makaiyu
 * @date 2020-04-28 15:28
 */
@Data
public class OauthUserRoleDTO implements Serializable {
    private static final long serialVersionUID = 4121541084774246620L;

    private Long roleId;

    private List<Long> addUserId;

    private List<Long> deleteUserId;

}
