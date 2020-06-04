package com.seaboxdata.auth.server.controller;

import com.seaboxdata.auth.api.controller.IOauthUserController;
import com.seaboxdata.auth.api.dto.*;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.server.bj.cas.service.ISynchDataService;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.auth.server.service.OauthUserService;
import com.seaboxdata.commons.exception.ServiceException;
import com.seaboxdata.commons.query.PaginationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@RestController
@Slf4j
public class OauthUserController implements IOauthUserController {

    @Autowired
    private OauthUserService oauthUserService;

    @Autowired
    private ISynchDataService synchDataService;

    @Value("${cas.bj.sync.password}")
    private String syncPassword;

    /**
     * @param oauthSaveUserDTO
     * @return void
     * @author makaiyu
     * @description 用户注册
     * @date 2019/5/13
     **/
    @Override
    public Boolean registerUser(@RequestBody OauthSaveUserDTO oauthSaveUserDTO) {

        if (oauthSaveUserDTO.getId() != null) {
            throw new ServiceException("999", "A new user cannot already have an ID");
        }

        return oauthUserService.registerUser(oauthSaveUserDTO);
    }

    /**
     * @param userIds
     * @return Boolean
     * @author makaiyu
     * @description 根据UserId  删除User
     * @date 9:31 2019/5/20
     **/
    @Override
    public Boolean deleteUserById(@RequestBody List<Long> userIds) {
        return oauthUserService.deleteUserById(userIds);
    }

    /**
     * @param username
     * @return com.seaboxdata.auth.model.SysUser
     * @author makaiyu
     * @description 根据username 获取user对象
     * @date 17:24 2019/5/13
     **/
    @Override
    public OauthUserVO getByUsername(String username) {
        OauthUser oauthUser = oauthUserService.getByUsername(username);
        OauthUserVO oauthUserVO = new OauthUserVO();
        BeanUtils.copyProperties(oauthUser, oauthUserVO);
        oauthUserVO.setUserId(oauthUser.getId());
        return oauthUserVO;
    }

    /**
     * @return java.util.List<OauthUser>
     * @author makaiyu
     * @description 获取全部用户
     * @date 9:56 2019/5/20
     **/
    @Override
    public PaginationResult<OauthUserVO> selectAllUser(@RequestBody(required = false) OauthUserNamePageDTO pageDTO) {
        return oauthUserService.selectAllUser(pageDTO);
    }

    /**
     * @param oauthSaveUserDTO
     * @return void
     * @author makaiyu
     * @description 修改用户信息及权限
     * @date 18:56 2019/5/27
     **/
    @Override
    public Boolean updateOauthUser(@RequestBody OauthSaveUserDTO oauthSaveUserDTO) {
        return oauthUserService.updateOauthUser(oauthSaveUserDTO);
    }

    /**
     * @param userIds
     * @return com.seaboxdata.auth.api.vo.OauthUserVO
     * @author makaiyu
     * @description 根据用户Id  获取用户信息
     * @date 15:59 2019/6/3
     **/
    @Override
    public List<OauthUserVO> selectUserByUserId(@RequestBody List<Long> userIds) {
        return oauthUserService.selectUserByUserId(userIds);
    }

    /**
     * @param roleId
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据roleId  寻找所属角色下的人员信息
     * @date 18:50 2019/6/3
     **/
    @Override
    public List<OauthUserVO> selectUserByRoleId(Long roleId) {
        return oauthUserService.selectUserByRoleId(roleId);
    }

    /**
     * @return java.lang.Long
     * @author makaiyu
     * @description 获取当前登陆用户信息
     * @date 9:57 2019/6/6
     **/
    @Override
    public OauthUserVO getLoginUser() {
        return oauthUserService.getLoginUser();
    }

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断username 是否存在
     * @date 14:41 2019/8/9
     **/
    @Override
    public Boolean checkUserName(@RequestBody OauthUserDTO user) {
        return oauthUserService.checkUserName(user);
    }

    /**
     * @param authUserParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据姓名或工号查询人员
     * @date 9:29 2019/8/14
     **/
    @Override
    public List<OauthUserVO> authSelectUserByNameOrCard(@RequestBody AuthUserParamDTO authUserParamDTO) {
        return oauthUserService.authSelectUserByNameOrCard(authUserParamDTO);
    }

    /**
     * @param userIds
     * @return OauthUserDTO
     * @author zdl
     * @description 根据userIds 获取List<OauthUserDTO></>对象, 仅仅查询oauth_user的数据，不关联其他信息
     * @date 10:00 2019/8/23
     **/
    @Override
    public List<OauthUserDTO> queryUsersByIds(@RequestBody List<Long> userIds) {
        return oauthUserService.queryUsersByIds(userIds);
    }

    /**
     * @param pwdDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改用户密码
     * @date 9:09 2019/9/26
     **/
    @Override
    public Boolean updateUserPwd(@RequestBody PwdDTO pwdDTO) {
        return oauthUserService.updateUserPwd(pwdDTO);
    }

    /**
     * @param password
     * @return java.lang.String
     * @author makaiyu
     * @description 同步bj-cas数据
     * @date 14:19 2019/10/28
     **/
    @Override
    public Boolean synchData(String password) {
        if (syncPassword.equals(password)) {
            synchDataService.synchData();
            return true;
        }
        return false;
    }

    /**
     * @param oauthSaveUserDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改用户对应角色
     * @date 17:03 2019/11/22
     **/
    @Override
    public Boolean updateUserRoleByUserId(@RequestBody OauthSaveUserDTO oauthSaveUserDTO) {
        return oauthUserService.updateUserRoleByUserId(oauthSaveUserDTO);
    }

    /**
     * @param userId, roleIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个角色
     * @date 14:36 2020-04-26
     **/
    @Override
    public Boolean saveOrUpdateRoleUser(Long userId, @RequestBody List<Long> roleIds) {
        return oauthUserService.saveOrUpdateRoleUser(userId, roleIds);
    }

    /**
     * @param userId, groupIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个分组
     * @date 14:36 2020-04-26
     **/
    @Override
    public Boolean saveOrUpdateGroupUser(Long userId, @RequestBody List<Long> groupIds) {
        return oauthUserService.saveOrUpdateGroupUser(userId, groupIds);
    }
}
