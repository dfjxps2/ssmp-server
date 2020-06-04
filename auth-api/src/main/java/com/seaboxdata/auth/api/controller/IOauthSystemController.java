package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.vo.OauthSystemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-30
 */
@FeignClient(contextId = "AuthSystem",
        name = FeignClientConfig.NAME, url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthSystemController {

    /**
     * @param oauthSystemDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthSystemVO>
     * @author makaiyu
     * @description 获取全部system (可模糊查)
     * @date 14:47 2019/7/25
     **/
    @PostMapping("/system/select/all")
    List<OauthSystemVO> selectAllSystem(@RequestBody OauthSystemDTO oauthSystemDTO);


}
