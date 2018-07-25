package com.violet.base.service;

import android.content.Intent;
import android.content.ServiceConnection;

import com.violet.core.service.CoreService;

/**
 * Created by kan212 on 2018/4/19.
 * <p>
 * 通过startService
 * Service会经历 onCreate 到onStart，然后处于运行状态，stopService的时候调用onDestroy方法
 * 没有调用stopService的话，Service会一直在后台运行
 * <p>
 * 通过bindService
 * Service会运行onCreate，然后是调用onBind， 这个时候调用者和Service绑定在一起。
 * 调用者退出了，Srevice就会调用onUnbind->onDestroyed方法。
 */

public abstract class BaseService extends CoreService {


    /**
     * 如果一个service通过startService 被start之后，多次调用startService 的话，service会多次调用onStart方法。
     * 多次调用stopService的话，service只会调用一次onDestroyed方法。
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    /**
     * #@link https://blog.csdn.net/robertcpp/article/details/51537084
     * 问题:Service先start再bind如何关闭service，为什么bindService可以跟Activity生命周期联动？
     */
    private void startThenBind() {
        /**
         * 在bind的Activity退出的时候,Service会执行unBind方法而不执行onDestory方法,因为有startService方法调用过,
         * 所以Activity与Service解除绑定后会有一个与调用者没有关连的Service存在
         */
    }

    private void startThenBindAndStop(){
        /**
         * Service的onDestory方法不会立刻执行,因为有一个与Service绑定的Activity,但是在Activity退出的时候,
         * 会执行onDestory,如果要立刻执行stopService,就得先解除绑定
         */
    }
    /**
     * 当一个服务没被onDestory()销毁之前，只有第一个启动它的客户端能调用它的onBind()和onUnbind()。
     */

}
