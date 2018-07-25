package com.violet.core.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.violet.core.util.LogUtil;

/**
 * Created by kan212 on 2018/4/19.
 */

public abstract class CoreService extends Service {

    public final String TAG = this.getClass().getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d("onBind() executed");
        return null;
    }

    /**
     * 只在第一次创建时调用，后面都会直接调用 onStartCommand
     */
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("onCreate() executed");

    }

    /**
     * 在 onCreate 后被调用
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     *
     * {@link #START_STICKY}如果service进程被kill掉，保留service的状态为开始状态，
     * 但不保留递送的intent对象。随后系统会尝试重新创建service，由于服务状态为开始状态，
     * 所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service，
     * 那么参数Intent将为null。
     *
     * {@link #START_NOT_STICKY} 非粘性的，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
     *
     * {@link #START_REDELIVER_INTENT} 重传Intent。使用这个返回值时，如果在执行完onStartCommand后，
     * 服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入
     *
     * {@link #START_STICKY_COMPATIBILITY} START_STICKY的兼容版本，但不保证服务被kill后一定能重启
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 启动线程，执行耗时任务
        LogUtil.d("onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.d("onConfigurationChanged() executed");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d("onUnbind() executed");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        LogUtil.d("onRebind() executed");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        // 关闭任务，清理资源
        LogUtil.d("onDestroy() executed");
        super.onDestroy();
    }
}
