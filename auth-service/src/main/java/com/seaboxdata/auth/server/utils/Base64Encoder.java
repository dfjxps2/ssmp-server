package com.seaboxdata.auth.server.utils;

import org.apache.tomcat.util.codec.binary.Base64;

/**
 * @author makaiyu
 * @date 2019/9/10 18:40
 */
public class Base64Encoder {

    /**
     * @param bytes
     * @return
     */
    public static byte[] decode(final byte[] bytes) {
        return Base64.decodeBase64(bytes);
    }

    /**
     * 二进制数据编码为BASE64字符串
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(final byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

}