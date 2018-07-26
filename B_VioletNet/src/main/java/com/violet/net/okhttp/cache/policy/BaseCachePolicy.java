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
package com.violet.net.okhttp.cache.policy;

import android.graphics.Bitmap;

import com.violet.net.http.HttpManager;
import com.violet.net.http.callback.AbsCallback;
import com.violet.net.http.callback.Callback;
import com.violet.net.http.convert.AbsConverter;
import com.violet.net.dispatcher.VtCall;
import com.violet.net.dispatcher.VtCallback;
import com.violet.net.http.model.Response;
import com.violet.net.http.request.Request;
import com.violet.net.okhttp.OkGo;
import com.violet.net.okhttp.cache.CacheEntity;
import com.violet.net.http.cache.CacheMode;
import com.violet.net.okhttp.convert.JsonConvert;
import com.violet.net.okhttp.db.CacheManager;
import com.violet.net.okhttp.exception.HttpException;
import com.violet.net.okhttp.utils.HeaderParser;
import com.violet.net.okhttp.utils.HttpUtils;
import com.violet.net.okhttp.utils.OkHttpUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Headers;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public abstract class BaseCachePolicy implements CachePolicy {

    protected Request request;
    protected volatile boolean canceled;
    protected volatile int currentRetryCount = 0;
    protected boolean executed;
    protected VtCall rawCall;
    protected Callback mCallback;
    protected CacheEntity cacheEntity;

    public BaseCachePolicy(Request request) {
        this.request = request;
    }

    @Override
    public boolean onAnalysisResponse(VtCall call, okhttp3.Response response) {
        return false;
    }

    @Override
    public CacheEntity prepareCache() {
        //check the config of cache
        if (request.getCacheKey() == null) {
            request.cacheKey(HttpUtils.createUrlFromParams(request.getBaseUrl(), request.getParams().urlParamsMap));
        }
        if (request.getCacheMode() == null) {
            request.cacheMode(CacheMode.NO_CACHE);
        }

        CacheMode cacheMode = request.getCacheMode();
        if (cacheMode != CacheMode.NO_CACHE) {
            //noinspection unchecked
            cacheEntity = (CacheEntity) CacheManager.getInstance().get(request.getCacheKey());
            HeaderParser.addCacheHeaders(request, cacheEntity, cacheMode);
            if (cacheEntity != null && cacheEntity.checkExpire(cacheMode, request.getCacheTime(), System.currentTimeMillis())) {
                cacheEntity.setExpire(true);
            }
        }

        if (cacheEntity == null || cacheEntity.isExpire() || cacheEntity.getData() == null || cacheEntity.getResponseHeaders() == null) {
            cacheEntity = null;
        }
        return cacheEntity;
    }

    @Override
    public synchronized VtCall prepareRawCall() throws Throwable {
        if (executed) throw HttpException.COMMON("Already executed!");
        executed = true;
        rawCall = OkHttpUtil.getRawCall(request);
        if (canceled) rawCall.cancel();
        return rawCall;
    }

    protected <T> Response requestNetworkSync() {
        try {
            okhttp3.Response response = rawCall.execute();
            int responseCode = response.code();

            //network error
            if (responseCode == 404 || responseCode >= 500) {
                return OkHttpUtil.error(false, request, response.headers(), HttpException.NET_ERROR(), responseCode);
            }
            AbsConverter<T, okhttp3.Response> converter = request.getConverter();
//            if(converter == null){
//                Class<T> responseClass = request.getResponseClass();
//                converter = new JsonConvert(responseClass);
//            }
            T body = null;
            if(request.getResponseClass() != null){
                body = converter.convertResponse(response, (Class<T>)(request.getResponseClass()));
            } else {
                body = converter.convertResponse(response);
            }
            //save cache when request is successful
            saveCache(response.headers(), body);
            return OkHttpUtil.success2(false, body, request, response.headers(), responseCode);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            if (throwable instanceof SocketTimeoutException && currentRetryCount < request.getRetryCount()) {
                currentRetryCount++;
                rawCall = OkHttpUtil.getRawCall(request);
                if (canceled) {
                    rawCall.cancel();
                } else {
                    requestNetworkSync();
                }
            }
            return OkHttpUtil.error(false, request, null, throwable, -1);
        }
    }

    protected <T> void requestNetworkAsync() {
        rawCall.enqueue(new VtCallback() {
            @Override
            public void onFailure(VtCall call, IOException e) {
                e.printStackTrace();
                if (e instanceof SocketTimeoutException && currentRetryCount < request.getRetryCount()) {
                    //retry when timeout
                    currentRetryCount++;
                    rawCall = OkHttpUtil.getRawCall(request);
                    if (canceled) {
                        rawCall.cancel();
                    } else {
                        rawCall.enqueue(this);
                    }
                } else {
                    if (!call.isCanceled()) {
                        Response error = OkHttpUtil.error2(false, request, null, e);
                        onError(error);
                    }
                }
            }

            @Override
            public void onResponse(VtCall call, okhttp3.Response response)  {
                int responseCode = response.code();

                //network error
                if (responseCode == 404 || responseCode >= 500) {
                    Response error = OkHttpUtil.error(false, request, response.headers(), HttpException.NET_ERROR(), responseCode);
                    onError(error);
                    return;
                }

                if (onAnalysisResponse(call, response)) return;

                try {
                    AbsConverter<T, okhttp3.Response> converter = request.getConverter();
//                    if(converter == null){
//                        AbsCallback<T> callback = (AbsCallback<T>) request.getCallback();
//                        converter = new JsonConvert<T>(callback.getResponseClass());
//                    }
                    T body = null;
                    if(request.getResponseClass() != null){
                        body = converter.convertResponse(response, (Class<T>)(request.getResponseClass()));
                    } else {
                        body = converter.convertResponse(response);
                    }

//                    T body = ((AbsConverter<T, okhttp3.>)(request.getConverter())).convertResponse(response);
                    //save cache when request is successfulResponse
                    saveCache(response.headers(), body);
                    Response success = OkHttpUtil.success2(false, body, request, response.headers(), responseCode);
                    onSuccess(success);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    Response error = OkHttpUtil.error(false, request, response.headers(), throwable, responseCode);
                    onError(error);
                }
            }
        });
    }

    /**
     * 请求成功后根据缓存模式，更新缓存数据
     *
     * @param headers 响应头
     * @param data    响应数据
     */
    private <T> void saveCache(Headers headers, T data) {
        if (request.getCacheMode() == CacheMode.NO_CACHE) return;    //不需要缓存,直接返回
        if (data instanceof Bitmap) return;             //Bitmap没有实现Serializable,不能缓存

        CacheEntity cache = HeaderParser.createCacheEntity(headers, data, request.getCacheMode(), request.getCacheKey());
        if (cache == null) {
            //服务器不需要缓存，移除本地缓存
            CacheManager.getInstance().remove(request.getCacheKey());
        } else {
            //缓存命中，更新缓存
            CacheManager.getInstance().replace(request.getCacheKey(), cache);
        }
    }

    protected void runOnUiThread(Runnable run) {
        OkGo.getInstance().getDelivery().post(run);
    }

    protected void runOnCallerOrSubThread(Runnable run) {
        if(HttpManager.getInstance().isUseAsync()){
            OkGo.getInstance().getExecutorService().execute(run);
        } else {
            if(run != null){
                run.run();
            }
        }
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {
        canceled = true;
        if (rawCall != null) {
            rawCall.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        if (canceled) return true;
        synchronized (this) {
            return rawCall != null && rawCall.isCanceled();
        }
    }
}
