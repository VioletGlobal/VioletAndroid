package com.violet.net.sports.request;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by kan212 on 2018/7/30.
 */

public enum VolleyManager {
    INSTANCE;

    VolleyManager() {
        requestRequestFinishedListener = new MyRequestFinishedListener();
    }

    private RequestQueue mRequestQueue;
    private MyRequestFinishedListener requestRequestFinishedListener;

    public void addRequest(Context context, Request<?> request) {
        if (null == mRequestQueue) {
            mRequestQueue = Volley.newRequestQueue(context);
            mRequestQueue.addRequestFinishedListener(requestRequestFinishedListener);
        }
        mRequestQueue.add(request);
    }

    class MyRequestFinishedListener implements RequestQueue.RequestFinishedListener {

        @Override
        public void onRequestFinished(Request request) {
            if (null != request && request instanceof VolleyRequestWithFinishListener) {
                ((VolleyRequestWithFinishListener) request).setFinished(true);
            }
        }
    }
}
