package com.violet.net.http;

import com.violet.net.http.model.Progress;

/**
 * Created by liuqun1 on 2018/2/4.
 */

public interface UploadInterceptor {
    void uploadProgress(Progress progress);
    boolean cancelled();
}
