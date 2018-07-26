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
package com.violet.net.okhttp.upload;

import android.content.ContentValues;

import com.violet.net.http.HttpManager;
import com.violet.net.http.UploadInterceptor;
import com.violet.net.http.callback.AbsCallback;
import com.violet.net.http.callback.CommonCallback;
import com.violet.net.dispatcher.VtCall;
import com.violet.net.http.model.Response;
import com.violet.net.http.request.Request;
import com.violet.net.http.server.upload.AbsUploadTask;
import com.violet.net.http.server.upload.UploadListener;
import com.violet.net.okhttp.db.UploadDBManager;
import com.violet.net.http.model.Progress;
import com.violet.net.okhttp.utils.HttpUtils;
import com.violet.net.okhttp.utils.OkHttpUtil;
import com.violet.net.okhttp.utils.OkLogger;
import com.violet.net.okhttp.OkUpload;
import java.io.Serializable;
import java.util.HashMap;


/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：上传任务类
 * 修订历史：
 * ================================================
 */
public class UploadTask<T> extends AbsUploadTask<T> {


//    private ThreadPoolExecutor executor;
//    private PriorityRunnable priorityRunnable;

    public UploadTask(String tag, Request request) {
        HttpUtils.checkNotNull(tag, "tag == null");
        progress = new Progress();
        progress.tag = tag;
        progress.url = request.getBaseUrl();
        progress.status = Progress.NONE;
        progress.totalSize = -1;
        progress.request = request;

//        executor = OkUpload.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    public UploadTask(Progress progress) {
        HttpUtils.checkNotNull(progress, "progress == null");
        this.progress = progress;
//        executor = OkUpload.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    @Override
    public UploadTask<T> priority(int priority) {
        progress.priority = priority;
        return this;
    }

    @Override
    public UploadTask<T> extra1(Serializable extra1) {
        progress.extra1 = extra1;
        return this;
    }

    @Override
    public UploadTask<T> extra2(Serializable extra2) {
        progress.extra2 = extra2;
        return this;
    }

    @Override
    public UploadTask<T> extra3(Serializable extra3) {
        progress.extra3 = extra3;
        return this;
    }

    @Override
    public UploadTask<T> save() {
        UploadDBManager.getInstance().replace(progress);
        return this;
    }

    @Override
    public UploadTask<T> register(UploadListener<T> listener) {
        if (listener != null) {
            listeners.put(listener.tag, listener);
        }
        return this;
    }

    @Override
    public void unRegister(UploadListener<T> listener) {
        HttpUtils.checkNotNull(listener, "listener == null");
        listeners.remove(listener.tag);
    }

    @Override
    public void unRegister(String tag) {
        HttpUtils.checkNotNull(tag, "tag == null");
        listeners.remove(tag);
    }

    @Override
    public UploadTask<T> start() {
//        if (OkUpload.getInstance().getTask(progress.tag) == null || UploadDBManager.getInstance().get(progress.tag) == null) {
//            throw new IllegalStateException("you must call UploadTask#save() before UploadTask#start()！");
//        }
        if (progress.status != Progress.WAITING && progress.status != Progress.LOADING) {
            postOnStart(progress);
            postWaiting(progress);
//            priorityRunnable = new PriorityRunnable(progress.priority, this);
//            executor.execute(priorityRunnable);
            startInternal();
        } else {
            OkLogger.w("the task with tag " + progress.tag + " is already in the upload queue, current task status is " + progress.status);
        }
        return this;
    }

    @Override
    public void restart() {
        pause();
        progress.status = Progress.NONE;
        progress.currentSize = 0;
        progress.fraction = 0;
        progress.speed = 0;
        UploadDBManager.getInstance().replace(progress);
        start();
    }

    /** 暂停的方法 */
    @Override
    public void pause() {
//        executor.remove(priorityRunnable);
        if (progress.status == Progress.WAITING) {
            postPause(progress);
        } else if (progress.status == Progress.LOADING) {
            progress.speed = 0;
            progress.status = Progress.PAUSE;
        } else {
            OkLogger.w("only the task with status WAITING(1) or LOADING(2) can pause, current status is " + progress.status);
        }
    }

    /** 删除一个任务,会删除下载文件 */
    @Override
    public AbsUploadTask<T> remove() {
        pause();
        UploadDBManager.getInstance().delete(progress.tag);
        //noinspection unchecked
        AbsUploadTask<T> task = (UploadTask<T>) OkUpload.getInstance().removeTask(progress.tag);
        postOnRemove(progress);
        return task;
    }

    private void startInternal(){
        HttpUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                progress.status = Progress.LOADING;
                postLoading(progress);
                try {
                    //noinspection unchecked
                    final Request request = progress.request;
                    if(request != null){
                        final VtCall rawCall = OkHttpUtil.getRawCall(request);
                        request.uploadInterceptor(new UploadInterceptor() {
                            @Override
                            public void uploadProgress(Progress innerProgress) {
                                if (rawCall.isCanceled()) return;
                                if (progress.status != Progress.LOADING) {
                                    rawCall.cancel();
                                    return;
                                }
                                progress.from(innerProgress);
                                postLoading(progress);
                            }

                            @Override
                            public boolean cancelled() {
                                return progress.status >= Progress.PAUSE;
                            }
                        });
                        HttpManager.getInstance().execute(progress.request, new CommonCallback<T>() {
                            @Override
                            public void onSuccess(Response<T> response) {
                                if (response.isSuccessful()) {
                                    postOnFinish(progress, response.body());
                                } else {
                                    postOnError(progress, response.getException());
                                }
                            }

                            @Override
                            public void onError(Response<T> response) {
                                postOnError(progress, response.getException());
                            }
                        });
                    }

//            response = OkHttpUtil.<T>adapt(request).execute();
                } catch (Exception e) {
                    postOnError(progress, e);
                    return;
                }
            }
        });

    }

    @Override
    public void run() {
        startInternal();
    }

    private void postOnStart(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.NONE;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onStart(progress);
                }
            }
        });
    }

    private void postWaiting(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.WAITING;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postPause(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.PAUSE;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postLoading(final Progress progress) {
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postOnError(final Progress progress, final Throwable throwable) {
        progress.speed = 0;
        progress.status = Progress.ERROR;
        progress.exception = throwable;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onError(progress);
                }
            }
        });
    }

    private void postOnFinish(final Progress progress, final T body) {
        progress.speed = 0;
        progress.fraction = 1.0f;
        progress.status = Progress.FINISH;
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onFinish(body, progress);
                }
            }
        });
    }

    private void postOnRemove(final Progress progress) {
        updateDatabase(progress);
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onRemove(progress);
                }
                listeners.clear();
            }
        });
    }

    private void updateDatabase(Progress progress) {
        ContentValues contentValues = Progress.buildUpdateContentValues(progress);
        UploadDBManager.getInstance().update(contentValues, progress.tag);
    }
}
