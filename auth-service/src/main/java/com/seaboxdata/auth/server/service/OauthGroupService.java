package com.seaboxdata.auth.server.service;

import com.seaboxdata.auth.api.dto.OauthGroupDTO;
import com.seaboxdata.auth.api.dto.OauthGroupParamDTO;
import com.seaboxdata.auth.api.dto.OauthUserGroupDTO;
import com.seaboxdata.auth.api.vo.OauthGroupVO;
import com.seaboxdata.auth.api.vo.OauthUserVO;
import com.seaboxdata.commons.query.PaginationResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-28
 */
public interface OauthGroupService {

    /**
     * @param oauthGroupDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 增加分组信息
     * @date 15:11 2019/5/31
     **/
    Boolean saveOauthGroup(@RequestBody OauthGroupDTO oauthGroupDTO);

    /**
     * @param oauthGroupParamDTO
     * @return java.util.List<com.seaboxdata.auth.api.vo.OauthGroupDTO>
     * @author makaiyu
     * @description 查询全部分组
     * @date 15:19 2019/5/31
     */
    List<OauthGroupVO> selectAllGroup(@RequestBody OauthGroupParamDTO oauthGroupParamDTO);

    /**
     * @param userId
     * @return java.util.List<java.lang.Long>
     * @author makaiyu
     * @description 根据用户Id 获取分组Id
     * @date 10:52 2020-04-02
     **/
    List<Long> selectGroupIdByUserId(@RequestParam("userId") Long userId);

    /**
     * @param groupIds
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 根据Id 删除分组
     * @date 15:26 2019/5/31
     **/
    Boolean deleteOauthGroup(@RequestBody List<Long> groupIds);

    /**
     * @param oauthGroupDTO
     * @return com.seaboxdata.auth.api.vo.OauthGroupVO
     * @author makaiyu
     * @description 根据分组Id  获取组内成员
     * @date 15:39 2019/6/3
     **/
    PaginationResult<OauthUserVO> selectUserByGroupId(@RequestBody OauthGroupDTO oauthGroupDTO);

    /**
     * @param oauthUserGroupDTO
     * @return java.lang.Boolean
     * @author makaiyu
     * @description 一个分组 -> 多个用户
     * @date 14:29 2020-04-26
     **/
    Boolean saveOrUpdateUserGroup(@RequestBody OauthUserGroupDTO oauthUserGroupDTO);
}
