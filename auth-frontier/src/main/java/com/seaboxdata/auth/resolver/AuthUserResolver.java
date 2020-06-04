package com.seaboxdata.auth.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.github.thewaychung.response.AmbryBaseResponse;
import com.seaboxdata.ambry.client.AmbryRestClient;
import com.seaboxdata.auth.api.controller.IOauthJxpmUserController;
import com.seaboxdata.auth.api.controller.IOauthLoginController;
import com.seaboxdata.auth.api.controller.IOauthUserController;
import com.seaboxdata.auth.api.dto.*;
import com.seaboxdata.auth.api.vo.OauthJxpmUserVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.api.vo.PaginationJxpmUserResult;
import com.seaboxdata.auth.config.RefreshTokenHandle;
import com.seaboxdata.auth.resolver.input.*;
import com.seaboxdata.auth.utlis.IpUtil;
import com.seaboxdata.commons.query.PaginationResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.context.GraphQLServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * @author makaiyu
 * @date 2019/5/22 9:48
 */
@Service
@Slf4j
public class AuthUserResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private IOauthUserController oauthUserController;

    private IOauthLoginController oauthLoginController;

    @Autowired
    private AmbryRestClient ambryRestClient;

    @Autowired
    private RefreshTokenHandle refreshTokenHandle;

    @Autowired
    private IOauthJxpmUserController oauthJxpmUserController;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${token.domain}")
    private String tokenDomain;

    public AuthUserResolver(IOauthUserController oauthUserController, IOauthLoginController oauthLoginController) {
        this.oauthUserController = oauthUserController;
        this.oauthLoginController = oauthLoginController;
    }


    public String selectIP(DataFetchingEnvironment env) {
        GraphQLServletContext context = env.getContext();
        HttpServletRequest httpServletRequest = context.getHttpServletRequest();
        return IpUtil.getIpAddr(httpServletRequest);
    }


    /**
     * @return PaginationResult<OauthUserVO>
     * @author makaiyu
     * @description 获取全部用户
     * @date 9:56 2019/5/20
     **/
    public PaginationResult<OauthUserVO> authSelectAllUser(AuthUserNamePageInput authUserNamePageInput) {
        OauthUserNamePageDTO oauthUserNamePageDTO = new OauthUserNamePageDTO();
        if (Objects.nonNull(authUserNamePageInput)) {
            BeanUtils.copyProperties(authUserNamePageInput, oauthUserNamePageDTO);
        }
        return oauthUserController.selectAllUser(oauthUserNamePageDTO);
    }

    /**
     * @param authUserParamInput
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据姓名或工号查询人员
     * @date 9:28 2019/8/14
     **/
    public List<OauthUserVO> authSelectUserByNameOrCard(AuthUserParamInput authUserParamInput) {
        AuthUserParamDTO authUserParamDTO = new AuthUserParamDTO();
        if (Objects.nonNull(authUserParamInput)) {
            BeanUtils.copyProperties(authUserParamInput, authUserParamDTO);
        }
        return oauthUserController.authSelectUserByNameOrCard(authUserParamDTO);
    }

    /**
     * @param oauthUserDTO
     * @return Boolean
     * @author makaiyu
     * @description 新建用户
     * @date 2019/5/13
     **/
    @PreAuthorize("hasAuthority('ucManager')")
    public Boolean registerUser(OauthSaveUserDTO oauthUserDTO) {
        return oauthUserController.registerUser(oauthUserDTO);
    }

    /**
     * @param userIds
     * @return Boolean
     * @author makaiyu
     * @description 根据UserId  删除User
     * @date 9:31 2019/5/20
     **/
    @PreAuthorize("hasAuthority('ucManager')")
    public Boolean deleteOauthUser(List<Long> userIds) {
        return oauthUserController.deleteUserById(userIds);
    }

    /**
     * @param oauthSaveUserDTO
     * @return void
     * @author makaiyu
     * @description 修改用户信息及权限
     * @date 18:56 2019/5/27
     **/
    public Boolean updateOauthUser(OauthSaveUserDTO oauthSaveUserDTO) {
        return oauthUserController.updateOauthUser(oauthSaveUserDTO);
    }

    /**
     * @param loginUserInput
     * @return LoginUserVO
     * @author makaiyu
     * @description 登陆
     * @date 10:35 2019/5/20
     **/
    public OauthUserVO authLogin(LoginUserInput loginUserInput) {

        LoginUserDTO loginUserDTO = new LoginUserDTO().setUsername(loginUserInput.getUsername())
                .setPassword(loginUserInput.getPassword())
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRememberMe(loginUserInput.getRememberMe());
        OauthUserVO oauthUserVO = oauthLoginController.loginUser(loginUserDTO);

        String address = "";

        try {
            String ipAddr = IpUtil.getIpAddr(httpServletRequest);

            log.info("ip addr : {} ", ipAddr);

//            address = AddressUtils.getAddresses("ip=" + ipAddr, "utf-8");
//            JSONObject jsonObject = JSONObject.parseObject(address);

//            AddressDTO addressDTO = jsonObject.getObject("data", AddressDTO.class);
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setUserId(oauthUserVO.getUserId())
                    .setUsername(oauthUserVO.getUsername())
                    .setTenantId(oauthUserVO.getTenantId())
                    .setIp(ipAddr);

            oauthLoginController.saveAddressLog(addressDTO);
        } catch (Exception e) {
            log.error("获取用户地址失败 ", e);
        }

        Cookie accessTokenCookie = new Cookie("access_token", "Bearer" + oauthUserVO.getAccessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setDomain(tokenDomain);
        if (Objects.nonNull(loginUserInput.getRememberMe()) && loginUserInput.getRememberMe()) {
            accessTokenCookie.setMaxAge(60 * 60 * 12 * 2 * 7);
        } else {
            accessTokenCookie.setMaxAge(60 * 60 * 12 * 2);
        }
        accessTokenCookie.setHttpOnly(true);

        Cookie refreshTokenCookie = new Cookie("refresh_token", oauthUserVO.getRefreshToken());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setDomain(tokenDomain);
        refreshTokenCookie.setMaxAge(60 * 60 * 12 * 2 * 30);
        refreshTokenCookie.setHttpOnly(true);

        httpServletResponse.addCookie(accessTokenCookie);
        httpServletResponse.addCookie(refreshTokenCookie);
        oauthUserVO.setAccessToken("");
        oauthUserVO.setRefreshToken("");
        return oauthUserVO;
    }


    /**
     * @param userIds
     * @return com.seaboxdata.auth.api.vo.OauthUserVO
     * @author makaiyu
     * @description 根据用户Id  获取用户信息
     * @date 15:59 2019/6/3
     **/
    public List<OauthUserVO> selectUserByUserId(List<Long> userIds) {
        return oauthUserController.selectUserByUserId(userIds);
    }

    /**
     * @param authRoleIdInput
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据roleId  寻找所属角色下的人员信息
     * @date 18:50 2019/6/3
     **/
    public List<OauthUserVO> selectUserByRoleId(AuthRoleIdInput authRoleIdInput) {
        return oauthUserController.selectUserByRoleId(authRoleIdInput.getRoleId());
    }

    /**
     * @return java.lang.Long
     * @author makaiyu
     * @description 获取当前登陆用户信息
     * @date 9:57 2019/6/6
     **/
    public OauthUserVO getLoginUser() {
        return oauthUserController.getLoginUser();
    }

    /**
     * @return com.seaboxdata.commons.utils.Resp
     * @author makaiyu
     * @description 退出登录
     * @date 13:59 2019/7/15
     **/
    public Boolean logoutUser() {
        Boolean flag = oauthLoginController.logout();
        if (flag) {
            refreshTokenHandle.addTokenCookie(httpServletResponse, "", "access_token", 0);
            refreshTokenHandle.addTokenCookie(httpServletResponse, "", "refresh_token", 0);
            try {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                request.getSession().invalidate();
            } catch (Exception e) {
                log.error("logout -> response is null");
            }
//            Cookie accessTokenCookie = new Cookie("JSESSIONID", "");
//            accessTokenCookie.setPath("/");
//            accessTokenCookie.setDomain("auth-service.dev.jinxin.cloud");
//            accessTokenCookie.setMaxAge(0);
//            accessTokenCookie.setHttpOnly(true);
//            httpServletResponse.addCookie(accessTokenCookie);
            return true;
        }
        return false;
    }

    /**
     * @param username
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断username 是否存在
     * @date 14:41 2019/8/9
     **/
    public Boolean checkUserName(String username, Long userId) {
        OauthUserDTO oauthUserDTO = new OauthUserDTO();
        oauthUserDTO.setUsername(username)
                .setId(userId);
        return oauthUserController.checkUserName(oauthUserDTO);
    }

    /**
     * @param ambryId
     * @return com.github.thewaychung.response.AmbryBaseResponse
     * @author makaiyu
     * @description 删除头像
     * @date 16:46 2019/9/9
     **/
    public AmbryBaseResponse deleteAmbryById(String ambryId) {
        AmbryBaseResponse ambryBaseResponse = new AmbryBaseResponse();
        try {
            ambryBaseResponse = ambryRestClient.deleteFile(ambryId);
        } catch (Exception e) {
            ambryBaseResponse.setMessage("删除失败 ambryId : {} " + ambryId);
            return ambryBaseResponse;
        }
        ambryBaseResponse.setCode(200);
        return ambryBaseResponse;
    }

    /**
     * @param pwdInput
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改用户密码
     * @date 9:09 2019/9/26
     **/
    public Boolean updateUserPwd(PwdInput pwdInput) {
        PwdDTO pwdDTO = new PwdDTO();
        BeanUtils.copyProperties(pwdInput, pwdDTO);
        return oauthUserController.updateUserPwd(pwdDTO);
    }

    /**
     * @param password
     * @return java.lang.String
     * @author makaiyu
     * @description 同步bj-cas数据
     * @date 14:19 2019/10/28
     **/
    public Boolean synchData(String password) {
        return oauthUserController.synchData(password);
    }

    /**
     * @param user
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改用户对应角色
     * @date 17:03 2019/11/22
     **/
    public Boolean updateUserRoleByUserId(OauthSaveUserDTO user) {
        return oauthUserController.updateUserRoleByUserId(user);
    }

    /**
     * @param userId, roleIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个角色
     * @date 14:36 2020-04-26
     **/
    public Boolean saveOrUpdateRoleUser(Long userId, List<Long> roleIds) {
        return oauthUserController.saveOrUpdateRoleUser(userId, roleIds);
    }


    /**
     * @param userId, groupIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个分组
     * @date 14:36 2020-04-26
     **/
    public Boolean saveOrUpdateGroupUser(Long userId, List<Long> groupIds) {
        return oauthUserController.saveOrUpdateGroupUser(userId, groupIds);
    }

    /**
     * @param pageDTO
     * @return com.seaboxdata.commons.query.PaginationResult<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 内部管理系统获取全部用户
     * @date 13:25 2020-05-11
     **/
    public PaginationJxpmUserResult<OauthJxpmUserVO> selectJxpmAllUser(OauthUserNamePageDTO pageDTO) {
        OauthUserNamePageDTO oauthUserNamePageDTO = new OauthUserNamePageDTO();
        if (Objects.nonNull(pageDTO)) {
            BeanUtils.copyProperties(pageDTO, oauthUserNamePageDTO);
        }
        return oauthJxpmUserController.selectJxpmAllUser(oauthUserNamePageDTO);
    }

}
