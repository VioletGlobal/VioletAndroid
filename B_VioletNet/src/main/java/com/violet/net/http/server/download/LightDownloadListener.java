package com.violet.net.http.server.download;

/**
 * Created by liuqun1 on 2018/3/6.
 */

public interface LightDownloadListener {
    void onSuccess(String path);

    void onError();
}
