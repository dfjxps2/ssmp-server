package com.seaboxdata.auth.server.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName LocalDateTimeUtil
 * @Description LocalDateTimeUtil相关转换方法
 * @Author long
 * @Date 2020/4/16 20:18
 * @Version 1.0
 **/
public class LocalDateTimeUtil {
    /**
     * string格式的时间转化为LocalDateTime
     * @param text
     * @return
     */
    public static LocalDateTime parseString(String text){
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String string=text.trim();
        if (string.length() == 0) {
            return null;
        }
        if (string.length() > 10 ) {
            if(string.charAt(10) == 'T'){
                if (string.endsWith("Z")) {
                    return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC);
                } else {
                    return LocalDateTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            }else if(string.charAt(10) == ' '){
                if(string.indexOf('.')>0){
                    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                }else{
                    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
            }else{
                throw new IllegalArgumentException("不是有效的日期格式");
            }
        } else if (string.length() == 10) {
            string += " 23:59:59";
            return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            throw new IllegalArgumentException("不是有效的日期格式");
        }
    }
    /**
     * LocalDateTime转换成long类型的timestamp时间戳
     * @param localDateTime
     * @return
     */
    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * @description: LocalDateTime转换成yyyy-MM-dd类型的时间格式
     * @author: fenghao
     * @date: 2019/11/12 13:48
     * @param: [localDateTime]
     * @return: java.lang.String
     */
    public static String timeFormatString(LocalDateTime localDateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dtf.format(localDateTime);
    }

}
