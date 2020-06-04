package com.seaboxdata.auth.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.seaboxdata.commons.utils.LocalDateTimeJsonDeserializer;
import com.seaboxdata.commons.utils.LocalDateTimeJsonSerializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author makaiyu
 * @date 2019/9/11 17:26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PlatformCodeVO implements Serializable {
    private static final long serialVersionUID = -1506960811136568371L;

    /** 平台可创建租户数量*/
    private Integer tenantUseCount;

    /** 平台使用到期时间 */
    @JsonSerialize(using = LocalDateTimeJsonSerializable.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime endTime;

}
