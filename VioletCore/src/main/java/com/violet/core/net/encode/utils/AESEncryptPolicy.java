package com.violet.core.net.encode.utils;



import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 */

public class AESEncryptPolicy implements ApiConfigController.IEncryptPolicy {

    @Override
    public byte[] encrypt(String key, String src) {
        return encryptInternal(key, src);
    }


    private byte[] encryptInternal(String password, String content) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/EBC/");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return cipher.doFinal(byteContent); // 加密
        } catch (Exception e) {
        }
        return null;
    }
}
