package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.*;
import com.seaboxdata.auth.api.vo.OauthJxpmUserVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.auth.api.vo.PaginationJxpmUserResult;
import com.seaboxdata.auth.server.model.OauthUser;
import com.seaboxdata.commons.query.PaginationResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
public interface OauthUserService {

    /**
     * @param username
     * @return com.seaboxdata.auth.model.SysUser
     * @author makaiyu
     * @description 根据username 获取user对象
     * @date 17:24 2019/5/13
     **/
    OauthUser getByUsername(@RequestParam("username") String username);

    /**
     * @param oauthSaveUserDTO
     * @return Boolean
     * @author makaiyu
     * @description 用户注册
     * @date 2019/5/13
     **/
    Boolean registerUser(@RequestBody OauthSaveUserDTO oauthSaveUserDTO);

    /**
     * @param userIds
     * @return Boolean
     * @author makaiyu
     * @description 根据UserId  删除User
     * @date 9:31 2019/5/20
     **/
    Boolean deleteUserById(@RequestBody List<Long> userIds);

    /**
     * @return java.util.List<OauthUser>
     * @author makaiyu
     * @description 获取全部用户
     * @date 9:56 2019/5/20
     **/
    PaginationResult<OauthUserVO> selectAllUser(@RequestBody(required = false) OauthUserNamePageDTO pageDTO);

    /**
     * @return java.util.List<java.lang.String>
     * @author makaiyu
     * @description 查询全部username
     * @date 15:57 2020-01-06
     **/
    List<String> selectAllUserName();

    /**
     * @param loginUserDTO
     * @return com.seaboxdata.commons.utils.Resp
     * @author makaiyu
     * @description 登陆
     * @date 10:35 2019/5/20
     **/
    OauthUserVO login(@RequestBody LoginUserDTO loginUserDTO);

    /**
     * @return com.seaboxdata.commons.utils.Resp
     * @author makaiyu
     * @description 退出登录
     * @date 13:59 2019/5/21
     **/
    Boolean logout();

    /**
     * @param oauthSaveUserDTO
     * @return void
     * @author makaiyu
     * @description 修改用户信息及权限
     * @date 18:56 2019/5/27
     **/
    Boolean updateOauthUser(@RequestBody OauthSaveUserDTO oauthSaveUserDTO);

    /**
     * @param userIds
     * @return com.seaboxdata.auth.api.vo.OauthUserVO
     * @author makaiyu
     * @description 根据用户Id  获取用户信息
     * @date 15:59 2019/6/3
     **/
    List<OauthUserVO> selectUserByUserId(@RequestBody List<Long> userIds);

    /**
     * @param roleId
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据roleId  寻找所属角色下的人员信息
     * @date 18:50 2019/6/3
     **/
    List<OauthUserVO> selectUserByRoleId(Long roleId);

    /**
     * @return java.lang.Long
     * @author makaiyu
     * @description 获取当前登陆用户信息
     * @date 9:57 2019/6/6
     **/
    OauthUserVO getLoginUser();

    /**
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断username 是否存在
     * @date 14:41 2019/8/9
     **/
    Boolean checkUserName(@RequestBody OauthUserDTO user);

    /**
     * @param authUserParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据姓名或工号查询人员
     * @date 9:29 2019/8/14
     **/
    List<OauthUserVO> authSelectUserByNameOrCard(@RequestBody AuthUserParamDTO authUserParamDTO);

    /**
     * @param userIds
     * @return OauthUserDTO
     * @author zdl
     * @description 根据userIds 获取List<OauthUserDTO></>对象, 仅仅查询oauth_user的数据，不关联其他信息
     * @date 10:00 2019/8/23
     **/
    List<OauthUserDTO> queryUsersByIds(@RequestBody List<Long> userIds);

    /**
     * @param user
     * @param tenantId
     * @return void
     * @author zdl
     * @description 新增用户 / 修改用户处理
     * @date 15:40 2019/5/30
     **/
    Long handleUserInfo(OauthSaveUserDTO user, Long tenantId);

    /**
     * @param pwdDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改用户密码
     * @date 9:09 2019/9/26
     **/
    Boolean updateUserPwd(@RequestBody PwdDTO pwdDTO);


    /**
     * @param oauthSaveUserDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改用户对应角色
     * @date 17:03 2019/11/22
     **/
    Boolean updateUserRoleByUserId(@RequestBody OauthSaveUserDTO oauthSaveUserDTO);

    /**
     * @param userNames
     * @return java.lang.Boolean
     * @author makaiyu
     * @description
     * @date 14:11 2019-12-09
     **/
    Boolean deleteUserByUsername(@RequestBody List<String> userNames);

    /**
     * @param addressDTO
     * @return void
     * @author makaiyu
     * @description 保存用户登陆日志
     * @date 14:38 2020-01-02
     **/
    void saveAddressLog(@RequestBody AddressDTO addressDTO);

    /**
     * @param userId
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据用户Id 退出用户
     * @date 09:59 2020-04-07
     **/
    Boolean logoutByUserId(Long userId);

    /**
     * @param userId, roleIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个角色
     * @date 14:36 2020-04-26
     **/
    Boolean saveOrUpdateRoleUser(Long userId, @RequestBody List<Long> roleIds);

    /**
     * @param userId, groupIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个分组
     * @date 14:36 2020-04-26
     **/
    Boolean saveOrUpdateGroupUser(Long userId, @RequestBody List<Long> groupIds);

    /**
     * @param pageDTO
     * @return com.seaboxdata.commons.query.PaginationResult<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 内部管理系统获取全部用户
     * @date 13:25 2020-05-11
     **/
    PaginationJxpmUserResult<OauthJxpmUserVO> selectJxpmAllUser(@RequestBody OauthUserNamePageDTO pageDTO);
}
