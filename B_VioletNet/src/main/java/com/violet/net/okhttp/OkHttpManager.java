package com.violet.net.okhttp;

import com.violet.net.dispatcher.VtCall;
import com.violet.net.dispatcher.VtHttpClient;
import com.violet.net.http.Config;
import com.violet.net.http.HttpManager;
import com.violet.net.http.IHttpManager;
import com.violet.net.http.cache.CacheMode;
import com.violet.net.http.callback.Callback;
import com.violet.net.http.model.Response;
import com.violet.net.http.request.Request;
import com.violet.net.okhttp.adapter.CacheCall;
import com.violet.net.okhttp.https.HttpsUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Created by liuqun1 on 2018/1/29.
 */
public class OkHttpManager implements IHttpManager {

    private Config mConfig;

    public OkHttpManager(){
        defaultConfig();
    }

    @Override
    public Config getConfig() {
        return mConfig;
    }

    public OkHttpManager(Config config){
        mConfig = config;
        if(mConfig == null){
            defaultConfig();
        } else {
//            mConfig.build();
        }
    }

    private void defaultConfig(){
        mConfig = new OkHttpConfig();
        mConfig.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        mConfig.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        mConfig.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        mConfig.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        mConfig.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        mConfig.setCacheMode(CacheMode.NO_CACHE);
        mConfig.setRetryCount(3);
//        mConfig.build();
    }

    @Override
    public Response execute(Request request) throws IOException {
        if(request != null){
            mConfig.buildIfNeeded();
            com.violet.net.http.request.Call call = request.getCall() == null?new CacheCall(request) : request.getCall();
//            request.call(call);
            return call.execute();
        }
        return null;
    }

    @Override
    public void execute(final Request request, final Callback callback) {
        if(request != null){
            mConfig.buildIfNeeded();
            OkGo.execute(request, callback);
        }
    }

    @Override
    public void cancelByTag(Object tag) {
        if (tag == null) return;
        OkHttpConfig okHttpConfig = (OkHttpConfig) HttpManager.getInstance().getConfig();
        if(okHttpConfig != null){
            VtHttpClient client = okHttpConfig.getSNOkHttpClient();
            if(client!=null){
                for (VtCall call : client.dispatcher().queuedCalls()) {
                    if (tag.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
                for (VtCall call : client.dispatcher().runningCalls()) {
                    if (tag.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
            }
        }
    }


    @Override
    public void cancelAll() {
        OkHttpConfig okHttpConfig = (OkHttpConfig) HttpManager.getInstance().getConfig();
        if(okHttpConfig != null){
            VtHttpClient client = okHttpConfig.getSNOkHttpClient();
            if(client!=null){
                for (VtCall call : client.dispatcher().queuedCalls()) {
                    call.cancel();
                }
                for (VtCall call : client.dispatcher().runningCalls()) {
                    call.cancel();
                }
            }
        }
    }
}
