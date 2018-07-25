package com.violet.base.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by kan212 on 2018/6/29.
 */

public class NetUtil {


    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

}
