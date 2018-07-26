package com.violet.net.http.server.upload;

import com.violet.net.http.model.Progress;
import com.violet.net.http.request.Request;
import java.util.List;
import java.util.Map;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public interface IUploadManager {

    IUploadTask request(String tag, Request<? extends Request> request);

    public void startAll();

    public void pauseAll();

    public void removeAll();

    public boolean hasTask(String tag);

    IUploadTask getTask(String tag);

    IUploadTask removeTask(String tag);

//    void addOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener);

//    void removeOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener);

    Map<String, AbsUploadTask<?>> getTaskMap();

//    UploadThreadPool getThreadPool();

    List<AbsUploadTask<?>> restore(List<Progress> progressList);

}
