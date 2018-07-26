package com.violet.net.http.server.upload;


import com.violet.net.http.server.ProgressListener;

/**
 * Created by liuqun1 on 2018/2/1.
 */

public abstract class UploadListener<T> implements ProgressListener<T> {
    public final Object tag;

    public UploadListener(Object tag) {
        this.tag = tag;
    }
}
