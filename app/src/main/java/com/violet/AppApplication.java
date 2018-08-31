package com.violet;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.violet.dagger.DaggerApplication;
import com.violet.imageloader.core.ImageLoader;
import com.violet.imageloader.core.ImageLoaderConfiguration;
import com.violet.module.IModule;

import java.util.List;

/**
 * Created by kan212 on 2018/4/18.
 */

public class AppApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化自己做的imageLoader
        initImageLoader(getApplicationContext());
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
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
