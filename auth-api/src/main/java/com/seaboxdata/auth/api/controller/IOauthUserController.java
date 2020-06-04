package com.seaboxdata.auth.api.controller;

import com.seaboxdata.auth.api.FeignClientConfig;
import com.seaboxdata.auth.api.dto.*;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.commons.query.PaginationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@FeignClient(contextId = "AuthUser",
        name = "AUTH-SERVER", url = FeignClientConfig.URL, configuration = FeignClientsConfiguration.class)
@RestController
public interface IOauthUserController {

    /**
     * @param oauthSaveUserDTO
     * @return Boolean
     * @author makaiyu
     * @description 新建用户
     * @date 2019/5/13
     **/
    @PostMapping("/user/register")
    Boolean registerUser(@RequestBody OauthSaveUserDTO oauthSaveUserDTO);

    /**
     * @param userIds
     * @return Boolean
     * @author makaiyu
     * @description 根据UserId  删除User
     * @date 9:31 2019/5/20
     **/
    @DeleteMapping("/user/delete")
    Boolean deleteUserById(@RequestBody List<Long> userIds);

    /**
     * @param username
     * @return com.seaboxdata.auth.model.SysUser
     * @author makaiyu
     * @description 根据username 获取user对象
     * @date 17:24 2019/5/13
     **/
    @GetMapping("/user/get/username")
    OauthUserVO getByUsername(@RequestParam("username") String username);

    /**
     * @return java.util.List<OauthUser>
     * @author makaiyu
     * @description 获取全部用户
     * @date 9:56 2019/5/20
     **/
    @PostMapping("/user/select/all")
    PaginationResult<OauthUserVO> selectAllUser(@RequestBody(required = false) OauthUserNamePageDTO pageDTO);

    /**
     * @param oauthSaveUserDTO
     * @return void
     * @author makaiyu
     * @description 修改用户信息及权限
     * @date 18:56 2019/5/27
     **/
    @PostMapping("/user/update")
    Boolean updateOauthUser(@RequestBody OauthSaveUserDTO oauthSaveUserDTO);

    /**
     * @param userIds
     * @return com.seaboxdata.auth.api.vo.OauthUserVO
     * @author makaiyu
     * @description 根据用户Id  获取用户信息
     * @date 15:59 2019/6/3
     **/
    @PostMapping("/user/select/userid")
    List<OauthUserVO> selectUserByUserId(@RequestBody List<Long> userIds);

    /**
     * @param roleId
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据roleId  寻找所属角色下的人员信息
     * @date 18:50 2019/6/3
     **/
    @GetMapping("/user/select/roleid")
    List<OauthUserVO> selectUserByRoleId(@RequestParam("roleId") Long roleId);

    /**
     * @return java.lang.Long
     * @author makaiyu
     * @description 获取当前登陆用户信息
     * @date 9:57 2019/6/6
     **/
    @PostMapping("/user/get/login/id")
    OauthUserVO getLoginUser();


    /**
     * @param user
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 判断username 是否存在
     * @date 14:41 2019/8/9
     **/
    @PostMapping("/user/check/username")
    Boolean checkUserName(@RequestBody OauthUserDTO user);

    /**
     * @param authUserParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthUserVO>
     * @author makaiyu
     * @description 根据姓名或工号查询人员
     * @date 9:29 2019/8/14
     **/
    @PostMapping("/select/user/name/card")
    List<OauthUserVO> authSelectUserByNameOrCard(@RequestBody AuthUserParamDTO authUserParamDTO);

    /**
     * @param userIds
     * @return OauthUserDTO
     * @author zdl
     * @description 根据userIds 获取List<OauthUserDTO></>对象, 仅仅查询oauth_user的数据，不关联其他信息
     * @date 10:00 2019/8/23
     **/
    @PostMapping("/user/queryUsersByIds")
    List<OauthUserDTO> queryUsersByIds(@RequestBody List<Long> userIds);


    /**
     * @param pwdDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改用户密码
     * @date 9:09 2019/9/26
     **/
    @PostMapping("/user/update/pwd")
    Boolean updateUserPwd(@RequestBody PwdDTO pwdDTO);

    /**
     * @param password
     * @return java.lang.String
     * @author makaiyu
     * @description 同步bj-cas数据
     * @date 14:19 2019/10/28
     **/
    @GetMapping("/bj/auth/synch")
    Boolean synchData(@RequestParam("password") String password);

    /**
     * @param oauthSaveUserDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 修改用户对应角色
     * @date 17:03 2019/11/22
     **/
    @PostMapping("/user/role/update")
    Boolean updateUserRoleByUserId(@RequestBody OauthSaveUserDTO oauthSaveUserDTO);


    /**
     * @param userId, roleIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个角色
     * @date 14:36 2020-04-26
     **/
    @PostMapping("/user/role/more")
    Boolean saveOrUpdateRoleUser(@RequestParam("userId") Long userId,
                                 @RequestBody List<Long> roleIds);


    /**
     * @param userId, groupIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个用户 -> 多个分组
     * @date 14:36 2020-04-26
     **/
    @PostMapping("/user/group/more")
    Boolean saveOrUpdateGroupUser(@RequestParam("userId") Long userId,
                                  @RequestBody List<Long> groupIds);

}
