package com.seaboxdata.auth.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author makaiyu
 * @date 2020-04-28 15:28
 */
@Data
public class OauthUserGroupDTO implements Serializable {

    private static final long serialVersionUID = 2486596228896880917L;

    private Long groupId;

    private List<Long> addUserId;

    private List<Long> deleteUserId;

}
