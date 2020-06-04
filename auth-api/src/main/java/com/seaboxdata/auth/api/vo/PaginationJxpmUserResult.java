package com.seaboxdata.auth.api.vo;

import com.seaboxdata.commons.query.PaginationResult;
import lombok.Data;

import java.util.List;

/**
 * @author makaiyu
 * @date 2020-05-11 14:03
 */
@Data
public class PaginationJxpmUserResult<OauthJxpmUserVO> extends PaginationResult {

    private static final long serialVersionUID = -15237756537984527L;

    private List<OauthJxpmUserVO> jxpmUsers;

}
