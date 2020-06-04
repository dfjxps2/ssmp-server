package com.seaboxdata.auth.server.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * CODE对应本地id
 * </p>
 *
 * @author zdl
 * @since 2020-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OauthTranCode implements Serializable {

    private static final long serialVersionUID = 4321996875508474341L;
    /** 主键id */
    @TableId(value = "id", type = IdType.INPUT)
    @ToString.Include
    private Long id;

    /**
     * 本地id
     */
    private Long localId;

    /**
     * ca集成code
     */
    private String caCode;

    /**
     * code对应本地id数据类型，用户user，角色role，机构dept
     */
    private String type;
}
