package com.violet.core.net.policy;

import com.violet.core.net.Method;
import com.violet.core.net.api.AbsApi;
import com.violet.net.dispatcher.VtPriority;
import com.violet.net.http.model.Response;


/**
 * Created by kan212 on 2018/7/27.
 */

public class OkHttpLibApiExecutor implements AbsApi.IApiExecutor{

    @Override
    public void execute(AbsApi api, AbsApi.IApiResultDispatcher dispatcher, VtPriority priority) {
        api.setPriority(priority);
        switch (api.getRequestMethod()) {
            case Method.GET:
            case Method.POST:
                sendPostOrGetRequest(api, dispatcher);
                break;
            default:
                break;
        }
    }

    private void sendPostOrGetRequest(AbsApi api, AbsApi.IApiResultDispatcher dispatcher) {

    }

    @Override
    public Response executeSync(AbsApi api) {
        return null;
    }

    @Override
    public void executeForReplaceUri(AbsApi api, AbsApi.IApiResultDispatcher dispatcher, boolean isMinPriority, String replaceUri) {

    }

    @Override
    public void cancel(Object tag) {

    }

    @Override
    public void cancelAll() {

    }
}
