package com.violet.net.http.server.download;

import com.violet.net.http.model.Progress;
import com.violet.net.http.request.Request;

import java.util.List;
import java.util.Map;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public class DownloadManager implements IDownloadManager {

    private IDownloadManager mImpl;

    private static volatile DownloadManager sInstance;

    private DownloadManager(){

    }

    public static DownloadManager getInstance(){
        if(sInstance == null){
            synchronized (DownloadManager.class){
                if(sInstance == null){
                    sInstance = new DownloadManager();
                }
            }
        }
        return sInstance;
    }

    public void init(IDownloadManager iDownloadManager){
        this.mImpl = iDownloadManager;
    }

    @Override
    public void config(DownloadConfig config) {
        this.mImpl.config(config);
    }

    @Override
    public IDownloadTask request(String tag, Request request) {
        return mImpl.request(tag, request);
    }

    @Override
    public IDownloadTask request(String tag, Request<? extends Request> request, String downFolder) {
        return mImpl.request(tag, request, downFolder);
    }

    @Override
    public List<? extends IDownloadTask> restore(List<Progress> progressList) {
        return mImpl.restore(progressList);
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
    public void removeAll(boolean isDeleteFile) {
        mImpl.removeAll(isDeleteFile);
    }

    @Override
    public String getFolder() {
        return mImpl.getFolder();
    }

    @Override
    public void setFolder(String folder) {
        mImpl.setFolder(folder);
    }

    @Override
    public boolean hasTask(String tag) {
        return mImpl.hasTask(tag);
    }

    @Override
    public IDownloadTask getTask(String tag) {
        return mImpl.getTask(tag);
    }

    @Override
    public IDownloadTask removeTask(String tag) {
        return mImpl.removeTask(tag);
    }

    @Override
    public Map<String, AbsDownloadTask> getTaskMap() {
        return mImpl.getTaskMap();
    }

//    @Override
//    public DownloadThreadPool getThreadPool() {
//        return mImpl.getThreadPool();
//    }

//    @Override
//    public void addOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        mImpl.addOnAllTaskEndListener(listener);
//    }
//
//    @Override
//    public void removeOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        mImpl.removeOnAllTaskEndListener(listener);
//    }
}
