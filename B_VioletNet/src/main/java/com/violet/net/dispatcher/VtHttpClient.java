package com.violet.net.dispatcher;

import okhttp3.Request;

/**
 * Created by kan212 on 2018/7/25.
 */

public class VtHttpClient implements VtCall.Factory{

    @Override
    public VtCall newCall(Request request, VtPriority priority) {
        return null;
    }
}
