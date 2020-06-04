package com.seaboxdata.auth.api.utils.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author makaiyu
 * @date 2019/8/9 15:48
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class Message implements Serializable {

    private static final long serialVersionUID = -2792316016299352207L;

    /** formats: `topicName:tags` */
    private String destination;

    /** the Object to use as payload */
    private Object payload;

    /** use this key to select queue. for example: orderId, productId ... */
    private String hashKey;
}
