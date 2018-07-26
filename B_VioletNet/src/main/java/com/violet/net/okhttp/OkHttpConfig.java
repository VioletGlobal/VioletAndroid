package com.violet.net.okhttp;

import android.app.Application;

import com.violet.net.dispatcher.VtDispatcher;
import com.violet.net.dispatcher.VtHttp;
import com.violet.net.dispatcher.VtHttpClient;
import com.violet.net.http.AbsConfig;
import com.violet.net.http.Config;
import com.violet.net.http.cache.CacheMode;
import com.violet.net.http.convert.AbsConverter;
import com.violet.net.http.model.HttpHeaders;
import com.violet.net.http.model.HttpParams;
import com.violet.net.okhttp.convert.JsonConvert;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public class OkHttpConfig extends AbsConfig<OkHttpClient.Builder> {
    private volatile AbsConverter converter;
    OkHttpClient.Builder builder;
    private CookieJar cookieJar;
    private volatile VtHttpClient VtHttpClient;

    public OkHttpConfig(){

    }

    @Override
    public void converter(AbsConverter converter) {
        this.converter = converter;
    }

    @Override
    public AbsConverter getConverter() {
        if(converter == null){
            synchronized (OkHttpConfig.class){
                if(converter == null){
                    converter = new JsonConvert();
                }
            }
        }
        return converter;
    }

    @Override
    public Config setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    @Override
    public Config setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return this;
    }

    @Override
    public Config setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    @Override
    public Config addCommonHeaders(HttpHeaders commonHeaders) {
        this.commonHttpHeaders = commonHeaders;
        return this;
    }

    @Override
    public Config addCommonParams(HttpParams commonParams) {
        this.commonHttpParams = commonParams;
        return this;
    }

    @Override
    public Config context(Application context) {
        OkGo.getInstance().init(context);
        return this;
    }

    public Config cookieJar(CookieJar cookieJar) {
        this.cookieJar = cookieJar;
        return this;
    }

    @Override
    public Config priorityPercent(int[] priorityPercent) {
          super.priorityPercent(priorityPercent);
          buildIfNeeded();
          if(VtHttpClient != null){
              VtDispatcher dispatcher = VtHttpClient.dispatcher();
              if(dispatcher != null){
                  dispatcher.setPriorityPercent(priorityPercent);
              }
          }
          return this;
    }

    @Override
    public void onInit(OkHttpClient.Builder initializer) {
    }

    private synchronized void build() {
        if(mBuilded){
            return;
        }
        if(builder == null){
            builder = new OkHttpClient.Builder();
        }

        builder.readTimeout(readTimeout, readTimeoutTimeUnit);
        builder.writeTimeout(writeTimeout, writeTimeoutTimeUnit);
        builder.connectTimeout(connectTimeout, connectTimeoutTimeUnit);
        if(cookieJar != null){
            builder.cookieJar(cookieJar);
        }
        if(sslSocketFactory != null && trustManager != null){
            builder.sslSocketFactory(sslSocketFactory, trustManager);
        }
        if(hostnameVerifier != null){
            builder.hostnameVerifier(hostnameVerifier);
        }

        onInit(builder);
        VtHttp.getInstance().realBuilder(builder);
        VtHttpClient = VtHttp.getInstance().client(priorityPercent);
        mBuilded = true;
    }

    @Override
    public void buildIfNeeded() {
        if(!mBuilded){
            build();
        }
    }

    public VtHttpClient getSNOkHttpClient(){
        return VtHttpClient;
    }

}
