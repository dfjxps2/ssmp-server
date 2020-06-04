package com.seaboxdata.auth.server.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.seaboxdata.commons.utils.LocalDateTimeJsonDeserializer;
import com.seaboxdata.commons.utils.LocalDateTimeJsonSerializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 租户信息表
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
public class OauthTenant implements Serializable {

    private static final long serialVersionUID = 8880255449939923274L;
    /** 主键Id */
    @ToString.Include
    private Long id;

    /** 租户名称 */
    @ToString.Include
    private String tenantName;

    /** 租户编码 */
    private String tenantCode;

    /** 描述 */
    private String tenantDesc;

    /** 状态 0:启用 1:未启用 */
    private Integer status;

    /** 租户级别-激活码Id */
    private Long tenantCodeId;

    /** 管理员用户Id  */
    private Long userId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = LocalDateTimeJsonSerializable.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime createTime;

    /** 修改时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonSerialize(using = LocalDateTimeJsonSerializable.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime updateTime;

}
