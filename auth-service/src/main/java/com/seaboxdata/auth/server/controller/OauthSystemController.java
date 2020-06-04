package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthSystemController;
import com.seaboxdata.auth.api.dto.OauthSystemDTO;
import com.seaboxdata.auth.api.vo.OauthSystemVO;
import com.seaboxdata.auth.server.service.OauthSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-30
 */
@Service
public class OauthSystemController implements IOauthSystemController {

    @Autowired
    private OauthSystemService oauthSystemService;

    /**
     * @param oauthSystemDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthSystemVO>
     * @author makaiyu
     * @description 获取全部system (可模糊查)
     * @date 14:47 2019/7/25
     **/
    @Override
    public List<OauthSystemVO> selectAllSystem(OauthSystemDTO oauthSystemDTO) {
        return oauthSystemService.selectAllSystem(oauthSystemDTO);
    }
}
