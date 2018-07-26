package com.violet.net.dispatcher;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.platform.Platform;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * Created by kan212 on 2018/7/26.
 */

public class VtRealCall implements VtCall, VtCall.Priority {

    private final VtHttpClient client;
    private final Request request;
    private final VtPriority priority;
    private final Call realCall;

    // Guarded by this.
    private boolean executed;

    public VtRealCall(VtHttpClient client, Request request, VtPriority priority) {
        this.client = client;
        this.request = request;
        if (priority == null) {
            priority = VtPriority.PRIORITY_MID;
        }
        this.priority = priority;

        OkHttpClient realClient = client.realClient();
        realCall = realClient.newCall(request);
    }

    public static VtRealCall newRealCall(VtHttpClient client, Request request, VtPriority priority) {
        VtRealCall call = new VtRealCall(client, request, priority);
        return call;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response execute() throws IOException {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        try {
            client.dispatcher().executed(this);
            Response result = proceed();
            if (result == null) throw new IOException("Canceled: " + "result is null");
            return result;
        } catch (IOException e) {
            throw e;
        } catch (Exception ex) {
            String url = request.url() == null ? "" : request.url().toString();
            throw new IOException("Canceled: " + ex.getMessage());
        } finally {
            client.dispatcher().finished(this);
        }
    }

    @Override
    public void enqueue(VtCallback responseCallback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        client.dispatcher().enqueue(new AsyncCall(responseCallback));
    }

    @Override
    public void cancel() {
        realCall.cancel();
    }

    @Override
    public synchronized boolean isExecuted() {
        return executed;
    }

    @Override
    public boolean isCanceled() {
        return realCall.isCanceled();
    }

    @Override
    public VtCall clone() {
        return VtRealCall.newRealCall(client,request,priority);
    }

    @Override
    public VtPriority priority() {
        return priority;
    }

    public class AsyncCall extends NamedRunnable implements VtCall.Priority {
        private final VtCallback responseCallback;

        public AsyncCall(VtCallback responseCallback) {
            super("OkHttp %s", redactedUrl());
            this.responseCallback = responseCallback;
        }

        public String host() {
            return request.url().host();
        }

        public Request request() {
            return request;
        }

        public VtRealCall get() {
            return VtRealCall.this;
        }

        @Override
        public VtPriority priority() {
            return priority;
        }

        @Override
        protected void execute() {
            boolean signalledCallback = false;
            try {
                Response response = proceed();
                if (realCall.isCanceled()) {
                    signalledCallback = true;
                    responseCallback.onFailure(VtRealCall.this, new IOException("Canceled"));
                } else {
                    signalledCallback = true;
                    responseCallback.onResponse(VtRealCall.this, response);
                }
            } catch (IOException e) {
                if (signalledCallback) {
                    // Do not signal the callback twice!
                    Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
                } else {
                    responseCallback.onFailure(VtRealCall.this, e);
                }
            } catch (Exception ex) {
                responseCallback.onFailure(VtRealCall.this, new IOException("Canceled: " + ex.getMessage()));
                String url = request.url() == null ? "" : request.url().toString();
            } finally {
                client.dispatcher().finished(this);
            }
        }
    }

    private Response proceed() throws IOException {
        return realCall.execute();
    }

    /**
     * Returns a string that describes this call. Doesn't include a full URL as that might contain
     * sensitive information.
     */
    private String toLoggableString() {
        return (isCanceled() ? "canceled " : "")
                + "priority[" + priority + "]"
                + " to " + redactedUrl();
    }

    private String redactedUrl() {
        return request.url().redact();
    }
}
