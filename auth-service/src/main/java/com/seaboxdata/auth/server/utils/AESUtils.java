package com.seaboxdata.auth.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@Slf4j
public class AESUtils {

    private static final String encodeRules = "dfjx";

    /**
     * 加密
     * 1.构造密钥生成器
     * 2.根据ecnodeRules规则初始化密钥生成器
     * 3.产生密钥
     * 4.创建和初始化密码器
     * 5.内容加密
     * 6.返回字符串
     */
    public static String AESEncode(String content) {
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(encodeRules.getBytes());
            keygen.init(192, random);
            //3.产生原始对称密钥
            SecretKey original_key = keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] raw = original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key = new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byte_encode = content.getBytes("utf-8");
            //9.根据密码器的初始化方式--加密：将数据加密
            byte[] byte_AES = cipher.doFinal(byte_encode);
            //10.将加密后的数据转换为字符串
            //这里用Base64Encoder中会找不到包
            //解决办法：
            //在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
            String AES_encode = Base64Encoder.encode(byte_AES);
            //11.将字符串返回
            return AES_encode;
        } catch (Exception e) {
            log.warn("AESEncode", e);
        }
        //如果有错就返加nulll
        return null;
    }


    /**
     * AES加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(192, new SecureRandom(encryptKey.getBytes()));

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }

    /**
     * 解密
     * 解密过程：
     * 1.同加密1-4步
     * 2.将加密后的字符串反纺成byte[]数组
     * 3.将加密内容解密
     */
    public static String AESDecode(String content) {
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(encodeRules.getBytes());
            keygen.init(192, random);
            //3.产生原始对称密钥
            SecretKey original_key = keygen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] raw = original_key.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKey key = new SecretKeySpec(raw, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            //8.将加密并编码后的内容解码成字节数组
            byte[] byte_content = Base64.decodeBase64(content);
            /*
             * 解密
             */
            byte[] byte_decode = cipher.doFinal(byte_content);
            String AES_decode = new String(byte_decode, "utf-8");
            return AES_decode;
        } catch (Exception e) {
            log.warn("AESDecode", e);
        }
        //如果有错就返加nulll
        return null;
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
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(192, new SecureRandom(decryptKey.getBytes()));

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }

    /**
     * @param userId
     * @param tenantUseCount
     * @return java.lang.String
     * @author makaiyu
     * @description 平台生成激活码
     * @date 13:31 2019/9/4
     */
    public static String generatorPlatformActivityCode(String userId, String tenantUseCount, String timesTamp) {
        String registerCode = "";
        String activationCode = "";
        try {
            // 生成注册码
            registerCode = AESUtils.AESEncode(
                    "tenantUseCount:" + tenantUseCount +
                            ",userId:" + userId +
                            ",timesTamp:" + timesTamp);

            log.info("生成注册码  registerCode : {} ", registerCode);

            // 生成激活码
            assert registerCode != null;
            activationCode = AESUtils.AESEncode(registerCode);
            log.info("生成激活码  activationCode : {} ", activationCode);
        } catch (Exception e) {
            log.warn("生成激活码失败！ 租户id:{} . 租户级别:{} , 注册码:{}", userId,
                    tenantUseCount, registerCode, e);
        }

        assert activationCode != null;
        return activationCode.replaceAll("\n", "").replaceAll("\r", "");
    }

    /**
     * @return java.lang.String
     * @author makaiyu
     * @description 租户生成激活码
     * @date 13:31 2019/9/4
     **/
    public static String generatorActivityCode(Long tenantId, Long tenantLevelId) {
        String registerCode = "";
        String activationCode = "";
        try {
            // 生成注册码
            registerCode = AESUtils.AESEncode(
                    "tenantLevelId:" + tenantLevelId +
                            ",tenantId:" + tenantId);

            log.info("生成注册码  registerCode : {} ", registerCode);

            // 生成激活码
            assert registerCode != null;
            activationCode = AESUtils.AESEncode(registerCode);
            log.info("生成激活码  activationCode : {} ", activationCode);
        } catch (Exception e) {
            log.warn("生成激活码失败！ 租户id:{} . 租户级别:{} , 注册码:{}", tenantId,
                    tenantLevelId, registerCode, e);
        }

        return activationCode.replaceAll("\n", "").replaceAll("\r", "");
    }

    /**
     * @param tenantId,     activationCode
     * @param tenantLevelId
     * @return java.lang.String
     * @author makaiyu
     * @description 校验激活码
     * @date 13:31 2019/9/4
     **/
    public static Boolean checkActivationCode(String tenantId, String tenantLevelId, String activationCode) {
        try {
            // 获取注册码
            String getActivationCode = AESUtils.AESDecode(activationCode);
            log.info("注册码:{}", getActivationCode);
            // 获取注册码中属性
            String getParam = AESUtils.AESDecode(getActivationCode);

            assert getParam != null;
            String[] split = getParam.split(",");

            String getTenantId = "";
            String getTenantLevelId = "";
            for (String s : split) {
                if (s.contains("tenantId")) {
                    getTenantId = s;
                }
                if (s.contains("tenantLevelId")) {
                    getTenantLevelId = s;
                }
            }

            if (!(getTenantId.equals("tenantId:" + tenantId)
                    && getTenantLevelId.equals("tenantLevelId:" + tenantLevelId))) {
                log.info("检验激活码失败");
                return false;
            }

        } catch (Exception e) {
            log.warn("检验激活码失败！ 传入激活码 : {}  ", activationCode);
            return false;
        }
        return true;
    }

    /**
     * @param userId, tenantUseCount,   activationCode
     * @return java.lang.String
     * @author makaiyu
     * @description 校验平台激活码
     * @date 13:31 2019/9/4
     **/
    public static Boolean checkPlatformActivationCode(String userId, String tenantUseCount,
                                                      Long timesTamp, String activationCode) {
        try {

            // 生成激活码
            String activityCode = generatorPlatformActivityCode(userId, tenantUseCount, timesTamp.toString());

            if (!activityCode.equals(activationCode)) {
                log.info("激活码与原赋予激活码不同");
                return false;
            }

            // 获取注册码
            String getActivationCode = AESUtils.AESDecode(activationCode);
            log.info("注册码:{}", getActivationCode);
            // 获取注册码中属性
            String getParam = AESUtils.AESDecode(getActivationCode);

            assert getParam != null;
            String[] split = getParam.split(",");

            String getUserId = "";
            String getTenantUseCount = "";
            String getTime = "";
            for (String s : split) {
                if (s.contains("userId")) {
                    getUserId = s;
                }
                if (s.contains("tenantUseCount")) {
                    getTenantUseCount = s;
                }
                if (s.contains("timesTamp")) {
                    getTime = s;
                }
            }

            if (!(getUserId.equals("userId:" + userId)
                    && getTenantUseCount.equals("tenantUseCount:" + tenantUseCount)
                    && getTime.equals("timesTamp:" + timesTamp))) {
                log.info("检验激活码失败");
                return false;
            }

        } catch (Exception e) {
            log.warn("检验激活码失败！ 传入激活码 : {}  ", activationCode);
            return false;
        }
        return true;
    }


}