package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.vo.TenantCodeVO;
import com.seaboxdata.auth.server.model.TenantCode;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 租户-级别-激活码 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-09-03
 */
public interface TenantCodeService {

    /**
     * @param activationCode
     * @return com.seaboxdata.auth.api.vo.TenantCodeVO
     * @author makaiyu
     * @description 校验激活码
     * @date 15:19 2019/9/3
     **/
    TenantCodeVO checkActivationCode(@RequestParam("activationCode") String activationCode);

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 校验当前登录用户是否存在激活的激活码
     * @date 11:37 2019/9/5
     **/
    Boolean checkTenantCodeStatus();

    /**
     * @param tenantId
     * @return com.seaboxdata.auth.server.model.TenantCode
     * @author makaiyu
     * @description 根据状态 获取租户-激活码数据
     * @date 14:37 2019/9/6
     **/
    TenantCode getTenantCodeByStatus(@RequestParam("tenantId") Long tenantId);

    /**
     * @param tenantId
     * @return java.lang.Integer
     * @author makaiyu
     * @description 获取当前登录用户的租户级别
     * @date 14:15 2019/9/10
     */
    Integer selectTenantLevelById(@RequestParam("tenantId") Long tenantId);
}
