package com.violet;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.violet.grape.VioletGrape;
import com.violet.module.IModule;

import java.util.List;

/**
 * Created by kan212 on 2018/4/12.
 */

public abstract class VioletGrapeApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (isMain()) {
            VioletGrape.init(this);
            VioletGrape.getInstance().registerModule(registerModule());
            VioletGrape.getInstance().attach(this);
            VioletGrape.getInstance().registerSMBus();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (isMain()) {
            VioletGrape.getInstance().onCreate();
        }
    }

    protected abstract List<IModule> registerModule();

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (isMain()) {
            VioletGrape.getInstance().onTerminate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isMain()) {
            VioletGrape.getInstance().onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (isMain()) {
            VioletGrape.getInstance().onLowMemory();
        }
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (isMain()) {
            VioletGrape.getInstance().onTrimMemory(level);
        }
    }

    /**
     * 是否是主进程
     *
     * @return true主进程、false其他进程
     */
    public boolean isMain() {
        return getPackageName().equals(getProcessName());
    }

    /**
     * 得到当前进程名字
     *
     * @return 进程名字
     */
    public String getProcessName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return null;
        }
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        if (processInfos == null) {
            return null;
        }

        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo appProcess : processInfos) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
