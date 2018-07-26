package com.violet.net.okhttp;

import com.violet.net.http.model.Progress;
import com.violet.net.http.request.Request;
import com.violet.net.http.server.upload.AbsUploadTask;
import com.violet.net.http.server.upload.IUploadManager;
import com.violet.net.http.server.upload.IUploadTask;

import java.util.List;
import java.util.Map;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public class OkUploadProxy implements IUploadManager {
    @Override
    public IUploadTask request(String tag, Request request) {
        return OkUpload.request(tag, request);
    }

    @Override
    public void startAll() {
        OkUpload.getInstance().startAll();
    }

    @Override
    public void pauseAll() {
        OkUpload.getInstance().pauseAll();
    }

    @Override
    public void removeAll() {
        OkUpload.getInstance().removeAll();
    }

    @Override
    public boolean hasTask(String tag) {
        return OkUpload.getInstance().hasTask(tag);
    }

    @Override
    public IUploadTask getTask(String tag) {
        return OkUpload.getInstance().getTask(tag);
    }

    @Override
    public IUploadTask removeTask(String tag) {
        return OkUpload.getInstance().removeTask(tag);
    }

//    @Override
//    public void addOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        OkUpload.getInstance().addOnAllTaskEndListener(listener);
//    }
//
//    @Override
//    public void removeOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        OkUpload.getInstance().removeOnAllTaskEndListener(listener);
//    }

    @Override
    public Map<String, AbsUploadTask<?>> getTaskMap() {
        return OkUpload.getInstance().getTaskMap();
    }

//    @Override
//    public UploadThreadPool getThreadPool() {
//        return OkUpload.getInstance().getThreadPool();
//    }

    @Override
    public List<AbsUploadTask<?>> restore(List<Progress> progressList) {
        return OkUpload.restore(progressList);
    }
}
