package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.vo.PlatformCodeVO;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 平台-激活码 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-09-06
 */
public interface PlatformCodeService {

    /**
     * @param activityCode
     * @return java.lang.Long
     * @author makaiyu
     * @description 校验平台激活码
     * @date 11:25 2019/9/6
     **/
    PlatformCodeVO checkPlatformCode(@RequestParam("activityCode") String activityCode);

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断当前平台用户是否已激活
     * @date 11:39 2019/9/6
     **/
    Boolean checkPlatformStatus();
}
