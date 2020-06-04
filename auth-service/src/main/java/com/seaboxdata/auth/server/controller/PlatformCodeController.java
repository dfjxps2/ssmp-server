package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IPlatformCodeController;
import com.seaboxdata.auth.api.vo.PlatformCodeVO;
import com.seaboxdata.auth.server.service.PlatformCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 平台-激活码 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-09-06
 */
@Service
public class PlatformCodeController implements IPlatformCodeController {

    @Autowired
    private PlatformCodeService platformCodeService;

    /**
     * @param activityCode
     * @return java.lang.Long
     * @author makaiyu
     * @description 校验平台激活码
     * @date 11:25 2019/9/6
     **/
    @Override
    public PlatformCodeVO checkPlatformCode(String activityCode) {
        return platformCodeService.checkPlatformCode(activityCode);
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断当前平台用户是否已激活
     * @date 11:39 2019/9/6
     **/
    @Override
    public Boolean checkPlatformStatus() {
        return platformCodeService.checkPlatformStatus();
    }
}
