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

import com.violet.net.http.callback.Callback;
import com.violet.net.http.model.Response;
import com.violet.net.http.request.Request;
import com.violet.net.okhttp.cache.CacheEntity;
import com.violet.net.okhttp.utils.OkHttpUtil;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RequestFailedCachePolicy extends BaseCachePolicy {

    public RequestFailedCachePolicy(Request request) {
        super(request);
    }

    @Override
    public void onSuccess(final Response success) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess(success);
                mCallback.onFinish();
            }
        };
        if(mCallback.callOnMainThread()){
            runOnUiThread(run);
        } else {
            runOnCallerOrSubThread(run);
        }
    }

    @Override
    public void onError(final Response error) {

        if (cacheEntity != null) {
            final Response cacheSuccess = OkHttpUtil.success2(true, cacheEntity.getData(), request, error.getHeaders());
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    mCallback.onCacheSuccess(cacheSuccess);
                    mCallback.onFinish();
                }
            };
            if(mCallback.callOnMainThread()){
                runOnUiThread(run);
            } else {
                runOnCallerOrSubThread(run);
            }
        } else {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    mCallback.onError(error);
                    mCallback.onFinish();
                }
            };
            if(mCallback.callOnMainThread()){
                runOnUiThread(run);
            } else {
                runOnCallerOrSubThread(run);
            }
        }
    }

    @Override
    public Response requestSync(CacheEntity cacheEntity) {
        try {
            prepareRawCall();
        } catch (Throwable throwable) {
            return OkHttpUtil.error2(false, request, null, throwable);
        }
        Response response = requestNetworkSync();
        if (!response.isSuccessful() && cacheEntity != null) {
            response = OkHttpUtil.success2(true, cacheEntity.getData(), request, cacheEntity.getResponseHeaders());
        }
        return response;
    }

    @Override
    public void requestAsync(CacheEntity cacheEntity, Callback callback) {
        mCallback = callback;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                mCallback.onStart(request);

                try {
                    prepareRawCall();
                } catch (Throwable throwable) {
                    Response error = OkHttpUtil.error2(false, request, null, throwable);
                    mCallback.onError(error);
                    return;
                }
                requestNetworkAsync();
            }
        };
        if(mCallback.callOnMainThread()){
            runOnUiThread(run);
        } else {
            runOnCallerOrSubThread(run);
        }
    }
}
