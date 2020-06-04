package com.seaboxdata.auth.api.utils;

import com.seaboxdata.auth.api.utils.domain.UserDetail;
import com.seaboxdata.auth.api.vo.OauthLoginUserVO;
import com.seaboxdata.commons.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * @author makaiyu
 * @date 2019/6/24 16:59
 */
@Slf4j
public class UserUtils {

    public static OauthLoginUserVO getUserDetails() {

        try {
            LinkedHashMap<String, LinkedHashMap<String, String>> principal = (LinkedHashMap<String, LinkedHashMap<String, String>>) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();

            Set<Map.Entry<String, LinkedHashMap<String, String>>> entries = principal.entrySet();

            Iterator<Map.Entry<String, LinkedHashMap<String, String>>> iterator = entries.iterator();

            String userId = "";
            String tenantId = "";

            while (iterator.hasNext()) {
                Map.Entry<String, LinkedHashMap<String, String>> next = iterator.next();
                if (!"principal".equals(next.getKey())) {
                    continue;
                }
                LinkedHashMap<String, String> value = next.getValue();
                if (value.containsKey("userId")) {
                    userId = String.valueOf(value.get("userId"));
                }
                if (value.containsKey("tenantId")) {
                    tenantId = String.valueOf(value.get("tenantId"));
                }
            }

            OauthLoginUserVO userVO = new OauthLoginUserVO();

            userVO.setUserId("".equals(userId) ? null : Long.valueOf(userId));
            userVO.setTenantId("".equals(tenantId) ? null : Long.valueOf(tenantId));

            if (Objects.isNull(userVO)) {
                throw new ServiceException("401", "用户未登录");
            }

            return userVO;
        } catch (Exception e) {
            try {
                UserDetail userDetail = (UserDetail) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();

                OauthLoginUserVO userVO = new OauthLoginUserVO();

                userVO.setUserId(userDetail.getUserId());
                userVO.setTenantId(userDetail.getTenantId());
                return userVO;
            } catch (Exception e1) {
                throw new ServiceException("401", "用户未登录");
            }
        }
    }
}
