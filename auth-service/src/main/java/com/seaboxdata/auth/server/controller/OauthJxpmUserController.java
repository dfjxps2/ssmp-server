package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthJxpmUserController;
import com.seaboxdata.auth.api.dto.OauthUserNamePageDTO;
import com.seaboxdata.auth.api.vo.OauthJxpmUserVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.api.vo.PaginationJxpmUserResult;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.commons.query.PaginationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@RestController
@Slf4j
public class OauthJxpmUserController implements IOauthJxpmUserController {

    @Autowired
    private OauthUserService oauthUserService;


    /**
     * @param pageDTO
     * @return com.seaboxdata.commons.query.PaginationResult<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 内部管理系统获取全部用户
     * @date 13:25 2020-05-11
     **/
    @Override
    public PaginationJxpmUserResult<OauthJxpmUserVO> selectJxpmAllUser(OauthUserNamePageDTO pageDTO) {
        return oauthUserService.selectJxpmAllUser(pageDTO);
    }

}
