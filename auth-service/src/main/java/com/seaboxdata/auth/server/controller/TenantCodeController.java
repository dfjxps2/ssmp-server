package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.ITenantCodeController;
import com.seaboxdata.auth.api.vo.TenantCodeVO;
import com.seaboxdata.auth.server.service.TenantCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 租户-级别-激活码 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-09-03
 */
@Service
public class TenantCodeController implements ITenantCodeController {

    @Autowired
    private TenantCodeService tenantCodeService;

    /**
     * @param activationCode
     * @return com.seaboxdata.auth.api.vo.TenantCodeVO
     * @author makaiyu
     * @description 校验激活码
     * @date 15:19 2019/9/3
     **/
    @Override
    public TenantCodeVO checkActivationCode(String activationCode) {
        return tenantCodeService.checkActivationCode(activationCode);
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验当前登录用户是否存在激活的激活码
     * @date 11:37 2019/9/5
     **/
    @Override
    public Boolean checkTenantCodeStatus() {
        return tenantCodeService.checkTenantCodeStatus();
    }

    /**
     * @return java.lang.Integer
     * @author makaiyu
     * @description 获取当前登录用户的租户级别
     * @date 14:15 2019/9/10
     **/
    @Override
    public Integer selectTenantLevelById(Long tenantId) {
        return tenantCodeService.selectTenantLevelById(tenantId);
    }
}
