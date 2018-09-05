package com.violet.module.player.dynamicload.internal;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

/**
 * Created by kan212 on 2018/9/4.
 * A plugin apk. Activities in a same apk share a same AssetManager, Resources
 * and DexClassLoader
 */

public class DLPluginPackage {
    public String packageName;
    public String defaultActivity;
    public DexClassLoader classLoader;
    public AssetManager assetManager;
    public Resources resources;
    public PackageInfo packageInfo;

    public DLPluginPackage(DexClassLoader loader, Resources resources,
                           PackageInfo packageInfo) {
        this.packageName = packageInfo.packageName;
        this.classLoader = loader;
        this.resources = resources;
        this.packageInfo = packageInfo;
        this.assetManager = resources.getAssets();
        this.defaultActivity = parseDefaultActivityName();
    }

    /**
     * #link https://blog.csdn.net/gaitiangai/article/details/51925624
     * @return
     */
    private String parseDefaultActivityName() {
        if (this.packageInfo.activities != null &&  this.packageInfo.activities.length > 0){
            return packageInfo.activities[0].name;
        }
        return "";
    }
}
