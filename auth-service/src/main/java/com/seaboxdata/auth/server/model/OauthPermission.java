package com.seaboxdata.auth.server.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.seaboxdata.commons.enums.AppKeyEnum;
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
 * 资源许可表
 * </p>
 *
 * @author makaiyu
 * @since 2019-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
public class OauthPermission implements Serializable {

    private static final long serialVersionUID = -3994636826724146890L;
    /** 主键id */
    @TableId(value = "id", type = IdType.INPUT)
    @ToString.Include
    private Long id;

    /** 权限名称 */
    private String permissionName;

    /** 权限码 */
    @ToString.Include
    private String permissionCode;

    /** 应用名称 */
    private AppKeyEnum appName;

    /** 父Id */
    private Long parentId;

    /** 资源详情 */
    private String description;

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
