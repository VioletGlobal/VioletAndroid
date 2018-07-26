package com.violet.net.http.server.upload;

import java.io.Serializable;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public interface IUploadTask<T , R extends IUploadTask> extends Runnable {
    IUploadTask<T, R> priority(int priority);

    IUploadTask<T, R> extra1(Serializable extra1);

    IUploadTask<T, R> extra2(Serializable extra2);

    IUploadTask<T, R> extra3(Serializable extra3);

    IUploadTask<T, R> save();

    IUploadTask<T, R> register(UploadListener<T> listener);

    void unRegister(UploadListener<T> listener);

    void unRegister(String tag);

    IUploadTask<T, R> start();

    void restart();

    void pause();

    IUploadTask<T, R> remove();
}
