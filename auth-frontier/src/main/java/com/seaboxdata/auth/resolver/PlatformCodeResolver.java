package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.IPlatformCodeController;
import com.seaboxdata.auth.api.vo.PlatformCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author makaiyu
 * @date 2019/9/6 13:12
 */
@Service
public class PlatformCodeResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IPlatformCodeController platformCodeController;

    @Autowired
    public PlatformCodeResolver(IPlatformCodeController platformCodeController) {
        this.platformCodeController = platformCodeController;
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断当前平台用户是否已激活
     * @date 11:39 2019/9/6
     **/
    public Boolean checkPlatformStatus() {
        return platformCodeController.checkPlatformStatus();
    }

    /**
     * @param activityCode
     * @return java.lang.Long
     * @author makaiyu
     * @description 校验平台激活码
     * @date 11:25 2019/9/6
     **/
    public PlatformCodeVO checkPlatformCode(String activityCode) {
        return platformCodeController.checkPlatformCode(activityCode);
    }

}


