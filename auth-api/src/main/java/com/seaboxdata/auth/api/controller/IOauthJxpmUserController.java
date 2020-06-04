package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.OauthUserNamePageDTO;
import com.seaboxdata.auth.api.vo.OauthJxpmUserVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.api.vo.PaginationJxpmUserResult;
import com.seaboxdata.commons.query.PaginationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@FeignClient(contextId = "AuthJxpmUser",
        name = "AUTH-SERVER", url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthJxpmUserController {

    /**
     * @param pageDTO
     * @return com.seaboxdata.commons.query.PaginationResult<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 内部管理系统获取全部用户
     * @date 13:25 2020-05-11
     **/
    @PostMapping("/user/jxpm/select/all")
    PaginationJxpmUserResult<OauthJxpmUserVO> selectJxpmAllUser(@RequestBody(required = false) OauthUserNamePageDTO pageDTO);

}
