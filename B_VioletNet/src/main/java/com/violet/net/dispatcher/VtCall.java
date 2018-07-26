package com.violet.net.dispatcher;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kan212 on 2018/7/25.
 */

public interface VtCall extends Cloneable{

    /**
     * Returns the original request that initiated this call.
     */
    Request request();

    /**
     * Invokes the request immediately, and blocks until the response can be processed or is in
     * error.
     * <p>
     * <p>To avoid leaking resources callers should close the {@link Response} which in turn will
     * close the underlying {@link ResponseBody}.
     * <p>
     * <pre>@{code
     *
     *   // ensure the response (and underlying response body) is closed
     *   try (Response response = client.newCall(request).execute()) {
     *     ...
     *   }
     *
     * }</pre>
     * <p>
     * <p>The caller may read the response body with the response's {@link Response#body} method. To
     * avoid leaking resources callers must {@linkplain ResponseBody close the response body} or the
     * Response.
     * <p>
     * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
     * not necessarily indicate application-layer success: {@code response} may still indicate an
     * unhappy HTTP response code like 404 or 500.
     *
     * @throws IOException           if the request could not be executed due to cancellation, a connectivity
     *                               problem or timeout. Because networks can fail during an exchange, it is possible that the
     *                               remote server accepted the request before the failure.
     * @throws IllegalStateException when the call has already been executed.
     */
    Response execute() throws IOException;

    /**
     * Schedules the request to be executed at some point in the future.
     * <p>
     * <p>The {@link VtHttpClient#dispatcher dispatcher} defines when the request will run: usually
     * immediately unless there are several other requests currently being executed.
     * <p>
     * <p>This client will later call back {@code responseCallback} with either an HTTP response or a
     * failure exception.
     *
     * @throws IllegalStateException when the call has already been executed.
     */
    void enqueue(VtCallback responseCallback);

    /**
     * Cancels the request, if possible. Requests that are already complete cannot be canceled.
     */
    void cancel();

    /**
     * Returns true if this call has been either {@linkplain #execute() executed} or {@linkplain
     * #enqueue(VtCallback) enqueued}. It is an error to execute a call more than once.
     */
    boolean isExecuted();

    boolean isCanceled();

    /**
     * Create a new, identical call to this one which can be enqueued or executed even if this call
     * has already been.
     */
    VtCall clone();

    interface Priority {
        VtPriority priority();
    }

    interface Factory {
        VtCall newCall(Request request, VtPriority priority);
    }

}
