package com.violet.net.http.server.upload;

import com.violet.net.http.model.Progress;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by liuqun1 on 2018/2/5.
 */

public abstract class AbsUploadTask<T> implements IUploadTask<T, AbsUploadTask> {
    public Progress progress;
    public Map<Object, UploadListener<T>> listeners;
    @Override
    public AbsUploadTask<T> priority(int priority) {
        return null;
    }

    @Override
    public AbsUploadTask<T> extra1(Serializable extra1) {
        return null;
    }

    @Override
    public AbsUploadTask<T> extra2(Serializable extra2) {
        return null;
    }

    @Override
    public AbsUploadTask<T> extra3(Serializable extra3) {
        return null;
    }

    @Override
    public AbsUploadTask<T> save() {
        return null;
    }

    @Override
    public AbsUploadTask<T> register(UploadListener<T> listener) {
        return null;
    }

    @Override
    public void unRegister(UploadListener<T> listener) {

    }

    @Override
    public void unRegister(String tag) {

    }

    @Override
    public AbsUploadTask<T> start() {
        return null;
    }

    @Override
    public void restart() {

    }

    @Override
    public void pause() {

    }

    @Override
    public AbsUploadTask<T> remove() {
        return null;
    }

    @Override
    public void run() {

    }
}
