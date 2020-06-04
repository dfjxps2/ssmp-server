package com.seaboxdata.auth.server.config.service;

import com.google.common.collect.Lists;
import com.seaboxdata.auth.api.utils.domain.UserDetail;
import com.seaboxdata.auth.server.bj.cas.synchrodata.CasUserUtils;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.service.OauthRoleService;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.auth.server.utils.EncryptUtil;
import com.seaboxdata.commons.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private OauthUserService oauthUserService;
    @Autowired
    private OauthRoleService oauthRoleService;
    @Value("${cas.bj.filter.enabled}")
    private Boolean casFilter;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        OauthUser user = oauthUserService.getByUsername(username);

        if (Objects.isNull(user)) {
            throw new ServiceException("603", "账号不存在");
        } else {
            List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();
            // 获取用户授权
            Set<String> permissionCodes = oauthRoleService.selectPermissionCodeByUserId(user.getId());

            // 声明用户授权
            permissionCodes.forEach(permissionCode -> {
                if (!StringUtils.isEmpty(permissionCode)) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permissionCode);
                    grantedAuthorities.add(grantedAuthority);
                }
            });

            String password = user.getPassword();

            if (casFilter) {
                password = EncryptUtil.encryptPassword(CasUserUtils.PASSWORD);
            }

            UserDetail userDetail = new UserDetail(user.getUsername(), password,
                    true, true,
                    true, true, grantedAuthorities);

            userDetail.setUserId(user.getId());
            userDetail.setTenantId(user.getTenantId());

            return userDetail;
        }
    }
}
