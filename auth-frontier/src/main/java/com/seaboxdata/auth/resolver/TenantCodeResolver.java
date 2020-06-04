package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.seaboxdata.auth.api.controller.ITenantCodeController;
import com.seaboxdata.auth.api.vo.TenantCodeVO;
import com.seaboxdata.commons.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author makaiyu
 * @date 2019/9/3 15:27
 */
@Service
public class TenantCodeResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private ITenantCodeController tenantCodeController;

    @Autowired
    public TenantCodeResolver(ITenantCodeController tenantCodeController) {
        this.tenantCodeController = tenantCodeController;
    }

    /**
     * @param activationCode
     * @return com.seaboxdata.auth.api.vo.TenantCodeVO
     * @author makaiyu
     * @description 校验激活码
     * @date 15:19 2019/9/3
     **/
    public TenantCodeVO checkActivationCode(String activationCode) {
        if (StringUtils.isEmpty(activationCode)) {
            throw new ServiceException("400", "传入激活码为null");
        }
        return tenantCodeController.checkActivationCode(activationCode);
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验当前登录用户是否存在激活的激活码
     * @date 11:37 2019/9/5
     **/
    public Boolean checkTenantCodeStatus() {
        return tenantCodeController.checkTenantCodeStatus();
    }

}
