package com.violet.net.http;

import com.violet.net.http.callback.Callback;
import com.violet.net.http.model.Response;
import com.violet.net.http.request.Request;

import java.io.IOException;

/**
 * Created by liuqun1 on 2018/1/29.
 */

public class HttpManager implements IHttpManager {
    private IHttpManager mHttpManager;

    private static volatile HttpManager sInstance;
    /**
     * 目前只有okhttp 用到
     * 构造请求的过程是否异步
     * see {@link com.sina.okhttp.cache.policy.CachePolicy#requestAsync}
     */
    private volatile boolean mIsAsync = true;

    private HttpManager(){

    }

    public static HttpManager getInstance(){
        if(sInstance == null){
            synchronized (HttpManager.class){
                if(sInstance == null){
                    sInstance = new HttpManager();
                }
            }
        }
        return sInstance;
    }


    public void init(IHttpManager iHttpManager){
        this.mHttpManager = iHttpManager;
    }

    @Override
    public Config getConfig() {
        return mHttpManager.getConfig();
    }

    @Override
    public Response execute(Request request) throws IOException {
        return mHttpManager.execute(request);
    }

    @Override
    public  void execute(Request request, Callback callback) {
        mHttpManager.execute(request, callback);
    }

    @Override
    public void cancelByTag(Object tag) {
        mHttpManager.cancelByTag(tag);
    }

    @Override
    public void cancelAll() {
        mHttpManager.cancelAll();
    }

    public boolean isAvailable(){
        return mHttpManager != null;
    }

    public void checkAvailable(){
        if(mHttpManager == null){
            throw new IllegalStateException("HttpManager not initialized yet");
        }
    }

    public void setUseAsync(boolean isAsync){
        mIsAsync = isAsync;
    }

    public boolean isUseAsync(){
        return mIsAsync;
    }
}
