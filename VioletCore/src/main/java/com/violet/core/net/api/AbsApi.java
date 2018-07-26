package com.violet.core.net.api;

import com.violet.net.dispatcher.VtPriority;
import com.violet.net.http.model.Response;

/**
 * Created by kan212 on 2018/7/26.
 */

public class AbsApi {

    public interface IApiResultDispatcher<T> {
        void onResponseOK(T data, AbsApi api);

        void onResponseError(Object o, AbsApi api, Throwable throwable);
    }

    public interface IApiExecutor {
        void execute(AbsApi api, IApiResultDispatcher dispatcher, VtPriority priority);

        Response executeSync(AbsApi api);

        void cancel(Object tag);

    }

}
