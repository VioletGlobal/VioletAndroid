package com.violet.net.http.server.upload;

import com.violet.net.http.model.Progress;
import com.violet.net.http.request.Request;

import java.util.List;
import java.util.Map;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public class UploadManager implements IUploadManager {

    private IUploadManager mImpl;

    private static volatile UploadManager sInstance;

    private UploadManager(){

    }

    public static UploadManager getInstance(){
        if(sInstance == null){
            synchronized (UploadManager.class){
                if(sInstance == null){
                    sInstance = new UploadManager();
                }
            }
        }
        return sInstance;
    }

    public void init(IUploadManager iUploadManager){
        this.mImpl = iUploadManager;
    }


    @Override
    public IUploadTask request(String tag, Request request) {
        return mImpl.request(tag, request);
    }

    @Override
    public void startAll() {
        mImpl.startAll();
    }

    @Override
    public void pauseAll() {
        mImpl.pauseAll();
    }

    @Override
    public void removeAll() {
        mImpl.removeAll();
    }

    @Override
    public boolean hasTask(String tag) {
        return mImpl.hasTask(tag);
    }

    @Override
    public IUploadTask getTask(String tag) {
        return mImpl.getTask(tag);
    }

    @Override
    public IUploadTask removeTask(String tag) {
        return mImpl.removeTask(tag);
    }

//    @Override
//    public void addOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        mImpl.addOnAllTaskEndListener(listener);
//    }

//    @Override
//    public void removeOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        mImpl.removeOnAllTaskEndListener(listener);
//    }

    @Override
    public Map<String, AbsUploadTask<?>> getTaskMap() {
        return mImpl.getTaskMap();
    }

//    @Override
//    public UploadThreadPool getThreadPool() {
//        return mImpl.getThreadPool();
//    }

    @Override
    public List<AbsUploadTask<?>> restore(List<Progress> progressList) {
        return mImpl.restore(progressList);
    }
}
