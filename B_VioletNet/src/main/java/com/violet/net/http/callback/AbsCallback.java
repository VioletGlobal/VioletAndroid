package com.violet.net.http.callback;


import com.violet.net.http.model.Response;
import com.violet.net.http.request.Request;

public abstract class AbsCallback<T> implements Callback<T> {

//    protected Class<T> clazz;
//
//    public Class<T> getResponseClass() {
//        return clazz;
//    }

    @Override
    public void onStart(Request request) {
    }


    @Override
    public void onCacheSuccess(Response<T> response) {
    }

    @Override
    public void onFinish() {
    }

//    @Override
//    public void uploadProgress(com.violet.net.http.model.Progress progress) {
//
//    }

//    @Override
//    public void downloadProgress(com.violet.net.http.model.Progress progress) {
//
//    }

    @Override
    public boolean callOnMainThread() {
        return false;
    }
}
