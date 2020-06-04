package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.vo.OauthSystemVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-30
 */
public interface OauthSystemService {

    /**
     * @param oauthSystemDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthSystemVO>
     * @author makaiyu
     * @description 获取全部system (可模糊查)
     * @date 14:47 2019/7/25
     **/
    List<OauthSystemVO> selectAllSystem(@RequestBody OauthSystemDTO oauthSystemDTO);
}
