package com.violet.core.util;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5 {

    public static String hexdigest(String string) {
        if (string == null) {
            return null;
        }
        String s = null;
        final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            final byte tmp[] = md.digest();
            final char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                final byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        } catch (final NoSuchAlgorithmException e) {
            Log.e("MD5", e.toString());
        }
        return s;
    }

    public static String hexdigest_16(String string) {
        String s = hexdigest(string);
        if (null != s) {
            s = s.substring(8, 24);
        }
        return s;
    }

    public static String hexdigest(byte[] data) {
        if (data == null) {
            return null;
        }
        String s = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        } catch (Exception e) {
        }
        return s;
    }

    public static String hexdigest(MessageDigest md) {
        if (md == null) {
            return null;
        }
        String s = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
        try {
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        } catch (Exception e) {

        }
        return s;
    }
}
