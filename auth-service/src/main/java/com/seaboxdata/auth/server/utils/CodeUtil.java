package com.seaboxdata.auth.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * @author makaiyu
 * @date 2019/9/4 9:46
 */
@Slf4j
public class CodeUtil {

    /**
     * @param register, tenantLevel, tenantName, tenantId, expire
     * @return java.lang.String
     * @author makaiyu
     * @description 获取激活码
     * @date 14:05 2019/9/4
     **/
    public static String generatorActivityCode(String register, String tenantLevel, String tenantName, String tenantId, String expire) throws Exception {
        SecretKeySpec key = new SecretKeySpec(register.getBytes("UTF-8"), 0, 16, "AES");
        Map<String, String> config = new HashMap<String, String>();
        config.put("Tenant_Level", tenantLevel);
        config.put("Tenant_Id", tenantId);
        config.put("Expire_Date", expire);
        config.put("Tenant_Name", tenantName);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        ObjectMapper MAPPER = new ObjectMapper();
        byte[] bytes = cipher.doFinal(MAPPER.writeValueAsString(config).getBytes("UTF-8"));
        return byte2Hex(bytes).toString().toUpperCase();
    }

    /**
     * @param tenantLevel, tenantName, tenantId, expire
     * @return java.lang.String
     * @author makaiyu
     * @description 获取注册码
     * @date 14:05 2019/9/4
     **/
    public static String generatorRegisterCode(String tenantLevel, String tenantName,
                                               String tenantId, String expire) throws Exception {
        String keyBuilder = tenantLevel + tenantName + tenantId + expire;
        SecretKeySpec key = new SecretKeySpec(keyBuilder.getBytes("UTF-8"), 0, 16, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(keyBuilder.getBytes("UTF-8"));
        return byte2Hex(bytes).toString().toUpperCase();
    }

    private static StringBuilder byte2Hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            CharSequence tsTemp = Integer.toHexString(b & 0x000000FF);
            if (tsTemp.length() == 1) {
                tsTemp = new StringBuilder(2).append('0').append(tsTemp);
            }
            builder.append(tsTemp);
        }
        return builder;
    }

    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(
                encryptStr.getBytes("UTF-8"), decryptKey);
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128, new SecureRandom(decryptKey.getBytes()));

        // 产生原始对称密钥
        SecretKey originalKey = keygen.generateKey();

        // 获得原始对称密钥的字节数组
        byte[] raw = originalKey.getEncoded();

        //5.根据字节数组生成AES密钥
        SecretKey key = new SecretKeySpec(raw, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        // 解码
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }

}
