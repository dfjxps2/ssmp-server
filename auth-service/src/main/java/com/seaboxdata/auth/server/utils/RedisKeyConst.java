package com.seaboxdata.auth.server.utils;

/**
 * @Author: 苏博
 * @Date: 2020/5/7 14:51
 * @Description: redis键值常量
 */
public class RedisKeyConst {

    public static final Long MIN = 60L;
    public static final Long HOUR = 60 * MIN;
    public static final Long DAY = 24 * HOUR;
    public static final Long WEEK = 7 * DAY;

    public static final String AHTU_All_ORGANiZATION = "auth:allOrganization:";

    public static final String AHTU_All_GROUP = "auth:allGroup:";

    public static final String AHTU_All_USER = "auth:allUser:";
}
