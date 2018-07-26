/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.violet.net.okhttp;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.violet.net.dispatcher.VtCall;
import com.violet.net.dispatcher.VtHttpClient;
import com.violet.net.http.cache.CacheMode;
import com.violet.net.http.callback.Callback;
import com.violet.net.http.model.HttpHeaders;
import com.violet.net.http.model.HttpParams;
import com.violet.net.http.request.DeleteRequest;
import com.violet.net.http.request.GetRequest;
import com.violet.net.http.request.HeadRequest;
import com.violet.net.http.request.OptionsRequest;
import com.violet.net.http.request.PatchRequest;
import com.violet.net.http.request.PostRequest;
import com.violet.net.http.request.PutRequest;
import com.violet.net.http.request.Request;
import com.violet.net.http.request.TraceRequest;
import com.violet.net.okhttp.adapter.CacheCall;
import com.violet.net.okhttp.cache.CacheEntity;
import com.violet.net.okhttp.cookie.CookieJarImpl;
import com.violet.net.okhttp.utils.HttpUtils;
import com.violet.net.okhttp.utils.OkHttpUtil;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：网络请求的入口类
 * 修订历史：
 * ================================================
 */
public class OkGo {

    public static final okhttp3.MediaType OK_MEDIA_TYPE_PLAIN = okhttp3.MediaType.parse("text/plain;charset=utf-8");
    public static final okhttp3.MediaType OK_MEDIA_TYPE_JSON = okhttp3.MediaType.parse("application/json;charset=utf-8");
    public static final okhttp3.MediaType OK_MEDIA_TYPE_STREAM = okhttp3.MediaType.parse("application/octet-stream");
    public static final long DEFAULT_MILLISECONDS = 60000;      //默认的超时时间
    public static long REFRESH_TIME = 300;                      //回调刷新时间（单位ms）

    private Application context;            //全局上下文
    private Handler mDelivery;              //用于在主线程执行的调度器
    private Handler mSubThreadDelivery;              //用于在子线程执行的调度器
    private OkHttpClient okHttpClient;      //ok请求的客户端
    private HttpParams mCommonParams;       //全局公共请求参数
    private HttpHeaders mCommonHeaders;     //全局公共请求头
    private int mRetryCount;                //全局超时重试次数
    private CacheMode mCacheMode;           //全局缓存模式
    private long mCacheTime;                //全局缓存过期时间,默认永不过期
    private ExecutorService executorService;

    private OkGo() {
        mDelivery = new Handler(Looper.getMainLooper());
//        HandlerThread handlerThread = new HandlerThread("okhttplib-callback-thread");
//        handlerThread.start();
//        mSubThreadDelivery = new Handler(handlerThread.getLooper());
        mRetryCount = 3;
        mCacheTime = CacheEntity.CACHE_NEVER_EXPIRE;
        mCacheMode = CacheMode.NO_CACHE;
        executorService = Executors.newSingleThreadExecutor();

//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
//        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
//        loggingInterceptor.setColorLevel(Level.INFO);
//        builder.addInterceptor(loggingInterceptor);
//
//        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
//        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
//        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
//
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
//        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
//        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
//        okHttpClient = builder.build();
    }

    public static OkGo getInstance() {
        return OkGoHolder.holder;
    }

    private static class OkGoHolder {
        private static OkGo holder = new OkGo();
    }

    /** get请求 */
    public static  GetRequest get(String url) {
        return new GetRequest(url);
    }

    /** post请求 */
    public static  PostRequest post(String url) {
        return new PostRequest(url);
    }

    /** put请求 */
    public static  PutRequest put(String url) {
        return new PutRequest(url);
    }

    /** head请求 */
    public static  HeadRequest head(String url) {
        return new HeadRequest(url);
    }

    /** delete请求 */
    public static  DeleteRequest delete(String url) {
        return new DeleteRequest(url);
    }

    /** options请求 */
    public static  OptionsRequest options(String url) {
        return new OptionsRequest(url);
    }

    /** patch请求 */
    public static  PatchRequest patch(String url) {
        return new PatchRequest(url);
    }

    /** trace请求 */
    public static  TraceRequest trace(String url) {
        return new TraceRequest(url);
    }

    /** 必须在全局Application先调用，获取context上下文，否则缓存无法使用 */
    public OkGo init(Application app) {
        context = app;
        return this;
    }

    /** 获取全局上下文 */
    public Context getContext() {
        HttpUtils.checkNotNull(context, "please call OkGo.getInstance().init() first in application!");
        return context;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

//    public Handler getSubThreadDelivery() {
//        return mSubThreadDelivery;
//    }

    public ExecutorService getExecutorService(){
        return executorService;
    }

    public OkHttpClient getOkHttpClient() {
        HttpUtils.checkNotNull(okHttpClient, "please call OkGo.getInstance().setOkHttpClient() first in application!");
        return okHttpClient;
    }

    /** 必须设置 */
    public OkGo setOkHttpClient(OkHttpClient okHttpClient) {
        HttpUtils.checkNotNull(okHttpClient, "okHttpClient == null");
        this.okHttpClient = okHttpClient;
        return this;
    }

    /** 获取全局的cookie实例 */
    public CookieJarImpl getCookieJar() {
        return (CookieJarImpl) okHttpClient.cookieJar();
    }

    /** 超时重试次数 */
    public OkGo setRetryCount(int retryCount) {
        if (retryCount < 0) throw new IllegalArgumentException("retryCount must > 0");
        mRetryCount = retryCount;
        return this;
    }

    /** 超时重试次数 */
    public int getRetryCount() {
        return mRetryCount;
    }

    /** 全局的缓存模式 */
    public OkGo setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }

    /** 获取全局的缓存模式 */
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    /** 全局的缓存过期时间 */
    public OkGo setCacheTime(long cacheTime) {
        if (cacheTime <= -1) cacheTime = CacheEntity.CACHE_NEVER_EXPIRE;
        mCacheTime = cacheTime;
        return this;
    }

    /** 获取全局的缓存过期时间 */
    public long getCacheTime() {
        return mCacheTime;
    }

    /** 获取全局公共请求参数 */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /** 添加全局公共请求参数 */
    public OkGo addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) mCommonParams = new HttpParams();
        mCommonParams.put(commonParams);
        return this;
    }

    /** 获取全局公共请求头 */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /** 添加全局公共请求参数 */
    public OkGo addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) mCommonHeaders = new HttpHeaders();
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /** 根据Tag取消请求 */
    public void cancelTag(Object tag) {
//        if (tag == null) return;
//        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
//            if (tag.equals(call.request().tag())) {
//                call.cancel();
//            }
//        }
//        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
//            if (tag.equals(call.request().tag())) {
//                call.cancel();
//            }
//        }
    }

    /** 根据Tag取消请求 */
    public static void cancelTag(VtHttpClient client, Object tag) {
        if (client == null || tag == null) return;
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

    /** 取消所有请求请求 */
    public void cancelAll() {
//        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
//            call.cancel();
//        }
//        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
//            call.cancel();
//        }
    }

    /** 取消所有请求请求 */
    public static void cancelAll(VtHttpClient client) {
        if (client == null) return;
        for (VtCall call : client.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (VtCall call : client.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    /** 非阻塞方法，异步请求，但是回调在子线程中执行 */
    public static void execute(Request r, Callback callback) {
        HttpUtils.checkNotNull(callback, "callback == null");

        r.setCallback(callback);
        com.violet.net.http.request.Call call = r.getCall() == null?new CacheCall(r) : r.getCall();
//        r.call(call);
        call.execute(callback);
    }

    /** 普通调用，阻塞方法，同步请求执行 */
    public static Response execute(Request r) throws IOException {
        return OkHttpUtil.getRawCall(r).execute();
    }
}
