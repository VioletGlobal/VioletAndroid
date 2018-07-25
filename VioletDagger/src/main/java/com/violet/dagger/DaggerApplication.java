package com.violet.dagger;

import com.violet.base.VioletBaseApplication;
import com.violet.dagger.refer.components.DaggerNetComponent;
import com.violet.dagger.refer.components.NetComponent;
import com.violet.dagger.refer.modules.NetModule;

/**
 * Created by kan212 on 2018/6/29.
 */

public abstract class DaggerApplication extends VioletBaseApplication {

    public NetComponent mNetComponent;
    public static DaggerApplication mDaggerApplication;


    @Override
    public void onCreate() {
        super.onCreate();
        mNetComponent =  DaggerNetComponent.builder()
                .netModule(new NetModule())
                .build();
    }

    @Override
    protected void initOnMainProcess() {
        if (mDaggerApplication == null) {
            synchronized (DaggerApplication.class) {
                if (mDaggerApplication == null) {
                    mDaggerApplication = this;
                }
            }
        }
    }
}
