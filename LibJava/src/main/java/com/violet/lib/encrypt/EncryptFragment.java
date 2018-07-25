package com.violet.lib.encrypt;

import android.content.Intent;
import android.util.Base64;
import android.view.View;

import com.violet.base.ui.fragment.BaseFragment;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by kan212 on 2018/5/15.
 * 加密和解密的相关信息
 * 我们可以实用JDK自带的实现，也可以使用一些开源的第三方库，例如Bouncy Castle（https://www.bouncycastle.org/）
 * 和comnons codec（https://commons.apache.org/proper/commons-codec/）。
 */

public class EncryptFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView(View parent) {

    }

    @Override
    protected void initData(Intent intent) {
        Base64("");
        jdkDES("");
        bcTripleDES("");
        jdsAES("");
    }

    /**
     * 　　AES是现在对称加密算法中最流行的算法之一。
     *
     * @param str
     */
    private void jdsAES(String str) {
        //生成key
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            //keyGenerator.init(128, new SecureRandom("seedseedseed".getBytes()));
            //使用上面这种初始化方法可以特定种子来生成密钥，这样加密后的密文是唯一固定的。
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] keyBytes = secretKey.getEncoded();
            //Key转换
            Key key = new SecretKeySpec(keyBytes, "AES");
            //加密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encodeResult = cipher.doFinal(str.getBytes());

            //解密
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodeResult = cipher.doFinal(str.getBytes());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private void bcTripleDES(String str) {
//        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * DES是一种基于56位密钥的对称算法，1976年被美国联邦政府的国家标准局确定为联邦资料处理标准（FIPS），
     * 随后在国际上广泛流传开来。现在DES已经不是一种安全的加密算法，已被公开破解，现在DES已经被高级加密标准（AES）所代替
     *
     * @param src
     */
    private void jdkDES(String src) {
        try {
            //生成密钥Key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(56);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] bytesKey = secretKey.getEncoded();

            //KEY转换
            DESKeySpec desKeySpec = new DESKeySpec(bytesKey);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
            Key convertSecretKey = secretKeyFactory.generateSecret(desKeySpec);

            //加密
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, convertSecretKey);
            byte[] encodeResult = cipher.doFinal(src.getBytes());
//            System.out.println("DESEncode :" + Hex.toHexString(encodeResult));

            //解密
            cipher.init(Cipher.DECRYPT_MODE, convertSecretKey);
            byte[] decodeResult = cipher.doFinal(src.getBytes());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 严格来说Base64并不是一种加密/解密算法，而是一种编码方式。Base64不生成密钥，通过Base64编码后的密文就可以直接“翻译”为明文，
     * 但是可以通过向明文中添加混淆字符来达到加密的效果。
     *
     * @param string
     */
    private void Base64(String string) {
        jdkBase64(string);
        commonsCodecBase64(string);
        bouncyCastleBase64();
    }

    private void jdkBase64(String str) {
        String encode = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        String decode = new String(Base64.decode(str, Base64.DEFAULT));
    }

    private void commonsCodecBase64(String str) {
        byte[] encodeBytes = Base64.encode(str.getBytes(), Base64.DEFAULT);
    }

    private void bouncyCastleBase64() {
    }

}
