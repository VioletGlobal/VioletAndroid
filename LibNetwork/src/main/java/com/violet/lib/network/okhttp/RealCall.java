package com.violet.lib.network.okhttp;

import java.io.IOException;

/**
 * Created by kan212 on 2018/6/8.
 */

public class RealCall implements Call{
    @Override
    public Request request() {
        return null;
    }

    @Override
    public Response execute() throws IOException {
        return null;
    }

    @Override
    public void enqueue(Callback responseCallback) {

    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Call clone() {
        return null;
    }
}
