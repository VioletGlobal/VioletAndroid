package com.violet.lib.android.sourceTemplate;

import android.content.Context;

/**
 * Created by kan212 on 2018/6/12.
 *
 * 安卓安装app的流程
 *
 */

public abstract class InstallTemplate {

    protected void template(Context context){
        realInstall(context);
        copy();
        parse();
        mainifest();
        dex();
        register();
        broadcast();
    }


    public abstract void realInstall(Context context);

    /**
     * 复制APK到/data/app目录下，解压并扫描安装包。
     */
    public abstract void copy();

    /**
     * 资源管理器解析APK里的资源文件。
     */
    public abstract void parse();


    /**
     * 解析AndroidManifest文件，并在/data/data/目录下创建对应的应用数据目录。
     */
    public abstract void mainifest();

    /**
     * 然后对dex文件进行优化，并保存在dalvik-cache目录下。
     */
    public abstract void dex();

    /**
     * 将AndroidManifest文件解析出的四大组件信息注册到PackageManagerService中。
     */
    public abstract void register();

    /**
     * 安装完成后，发送广播。
     */
    public abstract void broadcast();


}
