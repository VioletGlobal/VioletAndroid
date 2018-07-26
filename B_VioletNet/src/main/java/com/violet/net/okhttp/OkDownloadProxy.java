package com.violet.net.okhttp;

import com.violet.net.http.model.Progress;
import com.violet.net.http.request.Request;
import com.violet.net.http.server.download.AbsDownloadTask;
import com.violet.net.http.server.download.DownloadConfig;
import com.violet.net.http.server.download.IDownloadManager;
import com.violet.net.http.server.download.IDownloadTask;

import java.util.List;
import java.util.Map;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public class OkDownloadProxy implements IDownloadManager {
    private DownloadConfig mConfig;
    private volatile boolean inited = false;

    @Override
    public void config(DownloadConfig config) {
        if(config != null){
            mConfig = config;
//            OkDownload.getInstance().setFolder(config.getDownloadFolder());
        }
    }

    private void initIfNeeded(){
        if(!inited){
            synchronized (this){
                if(mConfig != null){
                    OkDownload.getInstance().setFolder(mConfig.getDownloadFolder());
                    inited = true;
                }
            }
        }
    }

    @Override
    public IDownloadTask request(String tag, Request request) {
        initIfNeeded();
        return OkDownload.request(tag, request);
    }

    @Override
    public IDownloadTask request(String tag, Request<? extends Request> request, String downFolder) {
        initIfNeeded();
        return OkDownload.request(tag, request, downFolder);
    }

    @Override
    public List<? extends IDownloadTask> restore(List<Progress> progressList) {
        return OkDownload.restore(progressList);
    }

    @Override
    public void startAll() {
        OkDownload.getInstance().startAll();
    }

    @Override
    public void pauseAll() {
        OkDownload.getInstance().pauseAll();
    }

    @Override
    public void removeAll() {
        OkDownload.getInstance().removeAll();
    }

    @Override
    public void removeAll(boolean isDeleteFile) {
        OkDownload.getInstance().removeAll(isDeleteFile);
    }

    @Override
    public String getFolder() {
        return OkDownload.getInstance().getFolder();
    }

    @Override
    public void setFolder(String folder) {
        OkDownload.getInstance().setFolder(folder);
    }

    @Override
    public boolean hasTask(String tag) {
        return OkDownload.getInstance().hasTask(tag);
    }

    @Override
    public IDownloadTask getTask(String tag) {
        return OkDownload.getInstance().getTask(tag);
    }

    @Override
    public IDownloadTask removeTask(String tag) {
        return OkDownload.getInstance().removeTask(tag);
    }

    @Override
    public Map<String, AbsDownloadTask> getTaskMap() {
        return OkDownload.getInstance().getTaskMap();
    }

//    @Override
//    public DownloadThreadPool getThreadPool() {
//        return OkDownload.getInstance().getThreadPool();
//    }

//    @Override
//    public void addOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        OkDownload.getInstance().addOnAllTaskEndListener(listener);
//    }
//
//    @Override
//    public void removeOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
//        OkDownload.getInstance().removeOnAllTaskEndListener(listener);
//    }
}
