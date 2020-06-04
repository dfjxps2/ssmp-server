package com.seaboxdata.auth.server.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: 苏博
 * @Date: 2020/5/7 18:14
 * @Description: 目前仅用于对使用redis缓存的地方进行标记
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Redis {

    String[] key() default "";
}
