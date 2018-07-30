package com.violet.net.sports.request;

import com.android.volley.Request;
import com.android.volley.Response;

/**
 * Created by kan212 on 2018/7/27.
 */

public abstract class VolleyRequestWithFinishListener<T> extends Request<T>{


    private boolean isFinished = false;//请求结束

    public VolleyRequestWithFinishListener(String url, Response.ErrorListener listener) {
        super(url, listener);
    }

    public VolleyRequestWithFinishListener(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    /**
     * 当前请求是否结束
     *
     * @return
     */
    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
}
