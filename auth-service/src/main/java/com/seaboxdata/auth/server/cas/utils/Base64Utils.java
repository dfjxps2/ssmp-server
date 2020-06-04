package com.seaboxdata.auth.server.cas.utils;

import org.apache.commons.codec.binary.Base64;

public class Base64Utils {

    //base64转字符串
    public static String base64ToString(String base64Str){
        return new String(Base64.decodeBase64(base64Str));
    }

}
