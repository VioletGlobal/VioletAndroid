package com.violet.base.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by kan212 on 2018/4/19.
 */

public abstract class BaseIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseIntentService(String name) {
        super(name);
    }

    /**
     * IntentService使用队列的方式将请求的Intent加入队列，然后开启了一个Worker Thread（工作线程）在处理队列中的Intent,
     * 对于异步的startService请求，IntentService会处理完成一个之后在处理第二个，
     * 每一个请求都会在一个单独的Worker Thread中处理，不会阻塞应用程序的主线程。
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
