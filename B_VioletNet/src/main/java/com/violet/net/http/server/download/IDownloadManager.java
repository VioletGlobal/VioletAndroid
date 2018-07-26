package com.violet.net.http.server.download;

import com.violet.net.http.model.Progress;
import com.violet.net.http.request.Request;

import java.util.List;
import java.util.Map;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public interface IDownloadManager {

    public void config(DownloadConfig config);

    IDownloadTask request(String tag, Request<? extends Request> request);

    IDownloadTask request(String tag, Request<? extends Request> request, String downFolder);

    List<? extends IDownloadTask> restore(List<Progress> progressList);

    void startAll();

    void pauseAll();

    void removeAll();

    void removeAll(boolean isDeleteFile);

    String getFolder();

    void setFolder(String folder);

    boolean hasTask(String tag);

    IDownloadTask getTask(String tag);

    IDownloadTask removeTask(String tag);

    Map<String, AbsDownloadTask> getTaskMap();

//    DownloadThreadPool getThreadPool();

//    void addOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener);

//    void removeOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener);

}
