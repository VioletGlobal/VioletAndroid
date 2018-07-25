package com.violet.core.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by kan212 on 2018/6/29.
 */

public class AppUtil {

    public static String getDeviceId(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}
