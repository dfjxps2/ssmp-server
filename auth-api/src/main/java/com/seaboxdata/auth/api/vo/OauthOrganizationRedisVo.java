package com.seaboxdata.auth.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 机构管理redis实体
 *
 * @author LiuZhanXi
 * @date 2020/3/24$ 18:05$
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OauthOrganizationRedisVo implements Serializable {
    private static final long serialVersionUID = -7967276001374586617L;
    /**
     * 主键id
     */
    @ToString.Include
    private Long id;

    /**
     * 父Id
     */
    private Long parentId;

    /**
     * 机构名称
     */
    private String organizationName;

    /** 机构编码 */
    private String organizationCode;

}

