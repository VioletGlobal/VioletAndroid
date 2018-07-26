package com.violet.net.http;

import com.violet.net.http.callback.Callback;
import com.violet.net.http.model.Response;
import com.violet.net.http.request.Request;

import java.io.IOException;

/**
 * Created by liuqun1 on 2018/1/29.
 */

public interface IHttpManager {
    public Config getConfig();
    /** 普通调用，阻塞方法，同步请求执行 */
    public Response execute(Request request) throws IOException;

    /** 非阻塞方法，异步请求，但是回调在子线程中执行 */
    public void execute(Request request, Callback callback);

    /** 根据Tag取消请求 */
    public void cancelByTag(Object tag);

    /** 取消所有请求请求 */
    public void cancelAll();
}
