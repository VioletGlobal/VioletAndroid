package com.violet;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.violet.dagger.DaggerApplication;
import com.violet.module.IModule;

import java.util.List;

/**
 * Created by kan212 on 2018/4/18.
 */

public class AppApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void initOnMainProcess() {
        super.initOnMainProcess();
        initContext();
    }

    private void initContext() {
        if (mInstance == null) {
            synchronized (AppApplication.class) {
                if (mInstance == null) {
                    mInstance = this;
                }
            }
        }
    }

    @Override
    protected List<IModule> registerModule() {
        return null;
    }
}
