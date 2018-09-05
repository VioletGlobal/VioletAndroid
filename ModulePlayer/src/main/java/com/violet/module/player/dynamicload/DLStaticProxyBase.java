package com.violet.module.player.dynamicload;

import com.violet.module.player.dynamicload.internal.DLPluginManager;
import com.violet.module.player.dynamicload.internal.DLPluginPackage;

/**
 * Created by kan212 on 2018/9/4.
 */

public abstract class DLStaticProxyBase {
    //包名
    protected String mPackageName;
    //动态加载需要的相关信息
    protected DLPluginPackage mPluginPackage;
    //动态加载的核心处理类
    protected DLPluginManager mPluginManager;

}
