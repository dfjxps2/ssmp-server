package com.seaboxdata.auth.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderItem implements Serializable {
    private String field;
    private OrderEnum order;
}