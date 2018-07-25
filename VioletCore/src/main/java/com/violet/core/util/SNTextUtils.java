package com.violet.core.util;

/**
 * Created by kan212 on 2018/4/16.
 */

public class SNTextUtils {

    /**
     * 如果str为null或者为""则返回true
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.toString().length() == 0;
    }

    /**
     * 如果str为null或者trim后为""则返回true
     *
     * @param str
     * @return
     */
    public static boolean isEmptyOrBlank(CharSequence str) {
        return isEmpty(str) || str.toString().trim().length() == 0;
    }
}
