package com.violet.net.http.server.download;

import java.io.Serializable;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public interface IDownloadTask<T extends IDownloadTask> extends Runnable {
    T folder(String folder);

    T fileName(String fileName);

    T priority(int priority);

    T extra1(Serializable extra1);

    T extra2(Serializable extra2);

    T extra3(Serializable extra3);

    T save();

    T register(DownloadListener listener);

    void unRegister(DownloadListener listener);

    void unRegister(String tag);

    void start();

    void restart();

    void pause();

    void remove();

    T remove(boolean isDeleteFile);
}
