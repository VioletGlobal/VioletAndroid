package com.violet.net.dispatcher;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.Util;

/**
 * Created by kan212 on 2018/7/26.
 */

public class VtHttp {

    private volatile VtHttpClient mClient;
    private OkHttpClient.Builder mBuilder;

    private static class Holder {
        public static final VtHttp sInstance = new VtHttp();
    }

    private VtHttp() {
    }

    public static VtHttp getInstance() {
        return Holder.sInstance;
    }

    public void realBuilder(OkHttpClient.Builder builder) {
        mBuilder = builder;
    }

    public VtHttpClient client(){
        return client(null);
    }

    public synchronized VtHttpClient client(int[] priorityPercent) {
        if (mClient == null) {
            int cpuCount = Runtime.getRuntime().availableProcessors();
            int corePoolSize = Math.min(8, cpuCount*2+1);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    Util.threadFactory("Vt VtDispatcher", false));
            if (mBuilder == null) {
                mBuilder = new OkHttpClient.Builder();
            }
            VtDispatcher dispatcher;
            if(priorityPercent == null){
                dispatcher = new VtDispatcher(executor);
            } else {
                dispatcher = new VtDispatcher(executor, priorityPercent);
            }
            dispatcher.setMaxRequests(corePoolSize);
            mClient = new VtHttpClient.Builder()
                    .dispatcher(dispatcher)
                    .realClient(mBuilder.build())
                    .build();
        }
        return mClient;
    }

}
