package com.violet.module;

import android.content.Context;
import android.content.res.Configuration;

import java.util.List;

/**
 * Created by kan212 on 2018/4/13.
 * 业务组件抽象层
 */

public interface IModule {
    /**
     * application.attach时调用
     *
     * @param context {@link Context}
     */
    void attach(Context context);

    /**
     * application.onCreate时调用
     */
    void onCreate();

    /**
     * application.onTerminate时调用
     */
    void onTerminate();

    /**
     * application.onConfigurationChanged时调用
     *
     * @param newConfig {@link Configuration}
     */
    void onConfigurationChanged(Configuration newConfig);

    /**
     * application.onLowMemory时调用
     */
    void onLowMemory();

    /**
     * application.onTrimMemory时调用
     *
     * @param level {@link android.content.ComponentCallbacks2}
     */
    void onTrimMemory(int level);

    /**
     * 消息订阅
     */
    List<Object> registerSMBus();

}
