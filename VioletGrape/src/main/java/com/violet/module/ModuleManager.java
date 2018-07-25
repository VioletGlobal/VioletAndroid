package com.violet.module;

import android.content.Context;
import android.content.res.Configuration;

import com.violet.center.CenterManager;

import java.util.List;

/**
 * Created by kan212 on 2018/4/13.
 */

public class ModuleManager implements IModule{

    private List<IModule> mModules;//module缓存

    /**
     * 添加module
     *
     * @param module 实现{@link IModule}接口的组件
     * @return true添加成功
     */
    public boolean addModule(IModule module) {
        return mModules.add(module);
    }

    /**
     * 批量添加module
     *
     * @param modules {@link IModule}实现集合
     * @return true添加成功
     */
    public boolean addModule(List<IModule> modules) {
        return mModules.addAll(modules);
    }

    @Override
    public void attach(Context context) {
        for (IModule module : mModules) {
            module.attach(context);
        }
    }

    @Override
    public void onCreate() {
        for (IModule module : mModules) {
            module.onCreate();
        }
    }

    @Override
    public void onTerminate() {
        for (IModule module : mModules) {
            module.onTerminate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        for (IModule module : mModules) {
            module.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        for (IModule module : mModules) {
            module.onLowMemory();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        for (IModule module : mModules) {
            module.onTrimMemory(level);
        }
    }

    @Override
    public List<Object> registerSMBus() {
        for (IModule module : mModules) {
            List<Object> list = module.registerSMBus();
            if (list != null && list.size() > 0) {
                for (Object subscription : list) {
                    CenterManager.getInstance().register(subscription);
                }
            }
        }
        return null;
    }

}
