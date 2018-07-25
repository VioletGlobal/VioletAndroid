package com.violet.core.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

/**
 * log的日志
 * Created by kan212 on 2018/4/19.
 */

public class LogUtil {

    private static boolean isDebug = true;

    private static boolean isLogSave = false;

    public static final String APP_TAG = "VioletAndroid_ ";


    public static void e(Object msg) {
        if (isDebug)
            Log.e(getTag(4), APP_TAG + (msg == null ? "null" : msg.toString()));
    }

    public static void d(Object msg) {
        if (isDebug)
            Log.d(getTag(4), APP_TAG + (msg == null ? "null" : msg.toString()));
    }

    public static void i(Object msg) {
        if (isDebug)
            Log.i(getTag(4), APP_TAG + (msg == null ? "null" : msg.toString()));
    }

    public static void w(Object msg) {
        if (isDebug)
            Log.w(getTag(4), APP_TAG + (msg == null ? "null" : msg.toString()));
    }

    private static File logFile;
    private static Date logDate;

    public static void s(Object msg) {
        if (isLogSave) {
//            if (null == logFile) {
//                File dir = FileUtil.getSdcardDirectory("log");
//                logFile = new File(dir.getPath(), "log.txt");
//                logDate = new Date();
//            }
//            logDate.setTime(System.currentTimeMillis());
//            StringBuilder sb = new StringBuilder();
//            sb.append(logDate.toString()).append(' ');
//            sb.append(getTag(4)).append(' ');
//            sb.append(msg == null ? "null" : msg.toString()).append('\n');
//            FileUtil.saveBytesToFile(sb.toString().getBytes(), logFile, true);
        }
    }

    public static void showTip(Context context, String msg) {
        if (null == context) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showTip(Context context, int msg) {
        if (null == context) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getTag(int level) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[level];
        StringBuilder sb = new StringBuilder();
        sb.append(getSimpleClassName(ste.getClassName()));
        sb.append('.');
        sb.append(ste.getMethodName());
        sb.append('(');
        sb.append(ste.getLineNumber());
        sb.append(')');
        return sb.toString();
    }

    public static String getSimpleClassName(String path) {
        int index = path.lastIndexOf('.');
        if (index < 0) {
            return path;
        } else {
            return path.substring(index + 1);
        }
    }
}
