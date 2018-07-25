package com.violet.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by kan212 on 2018/4/13.
 */

public class VioletBaseCore {

    private volatile static VioletBaseCore mInstance;
    private static Context mContext;
    private Handler mHandler;

    public VioletBaseCore() {
    }

    public static VioletBaseCore getInstance() {
        if (mInstance == null) {
            synchronized (VioletBaseCore.class) {
                if (mInstance == null) {
                    mInstance = new VioletBaseCore();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public Context getContext() {
        return mContext;
    }

    public Handler getHandler() {
        return mHandler;
    }
}
