package com.violet.net.dispatcher;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by kan212 on 2018/7/25.
 */

public class VtHttpClient implements VtCall.Factory{

    private VtDispatcher dispatcher;
    private OkHttpClient realClient;

    public VtHttpClient(Builder builder) {
        dispatcher = builder.dispatcher;
        realClient = builder.realClient;
    }

    @Override
    public VtCall newCall(Request request, VtPriority priority) {
        return VtRealCall.newRealCall(this,request,priority);
    }

    public OkHttpClient realClient() {
        return realClient;
    }

    public VtDispatcher dispatcher() {
        return dispatcher;
    }

    public static final class Builder {
        private VtDispatcher dispatcher;
        private OkHttpClient realClient;

        public Builder() {
        }

        public Builder dispatcher(VtDispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        public Builder realClient(OkHttpClient realClient) {
            this.realClient = realClient;
            return this;
        }

        public VtHttpClient build() {
            return new VtHttpClient(this);
        }
    }
}
