package com.violet.module.player.sdk.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.violet.module.player.dynamicload.DLProxyManager;
import com.violet.module.player.dynamicload.IDLProxyBase;
import com.violet.module.player.sdk.PluginManager;

/**
 * Created by kan212 on 2018/9/5.
 * 初始化的类
 */

public class VDApplication implements IDLProxyBase{

    static VDApplication instance = new VDApplication();
    private Handler mMainHandler;
    private Context mContext;

    private PluginManager mPluginManager;

    public VDApplication(){
        DLProxyManager.getInstance().put(getClass(),this);
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void setContext(Context context){
        if (null == mContext){
            mContext = context.getApplicationContext();
            mPluginManager = new PluginManager(context);
            mPluginManager.initPlayer(false);
        }
    }

    @Override
    public void install() {

    }

    @Override
    public void uninstall() {

    }
}
