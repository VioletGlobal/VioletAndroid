package com.violet.core.net;


import com.violet.core.net.api.AbsApi;
import com.violet.core.net.policy.OkHttpLibApiExecutor;
import com.violet.net.dispatcher.VtPriority;
import com.violet.net.http.model.Response;

import java.util.HashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Created by kan212 on 2018/7/26.
 */

public class ApiManager {

    public AbsApi.IApiExecutor executor;
    private static volatile ApiManager sManager = null;
    private HashMap<String, String> mCommonParamsMap;

    public static ApiManager getInstance() {
        if (sManager == null) {
            synchronized (ApiManager.class) {
                if (sManager == null) {
                    sManager = new ApiManager();
                }
            }
        }
        return sManager;
    }

    private ApiManager() {
        this.executor = new OkHttpLibApiExecutor();
//        this.mCommonParamsMap = ApiCommonParams.getInstance().getsApiCommonParamsMap();
    }

    public ApiManager setExecutor(AbsApi.IApiExecutor executor) {
        this.executor = executor;
        return this;
    }

    public <T> Response<T> executeSync(final ApiBase<T> api) {
        return executor.executeSync(api);
    }


    public <T> Observable<T> doRxApi(final ApiBase<T> api) {
        return doRxApiAsync(api, VtPriority.PRIORITY_MID);
    }

    public <T> Observable<T> doHighRxApi(final ApiBase<T> api) {
        return doRxApiAsync(api, VtPriority.PRIORITY_HIGH);
    }

    public <T> Observable<T> doLowRxApi(final ApiBase<T> api) {
        return doRxApiAsync(api, VtPriority.PRIORITY_LOW);
    }

    public <T> Observable<T> doRxApiSync(final ApiBase<T> api, final VtPriority priority) {
        return doRxApiAsync(api, priority)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Observable<T> doRxApiSync(final ApiBase<T> api) {
        return doRxApiSync(api, VtPriority.PRIORITY_MID);
    }

    public <T> Observable<T> doHighRxApiSync(final ApiBase<T> api) {
        return doRxApiSync(api, VtPriority.PRIORITY_HIGH);
    }

    public <T> Observable<T> doLowRxApiSync(final ApiBase<T> api) {
        return doRxApiSync(api, VtPriority.PRIORITY_LOW);
    }

    public <T> Observable<T> doRxApiAsync(final ApiBase<T> api, final VtPriority priority) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(final FlowableEmitter<T> e) throws Exception {
                api.addCommonRequestParams(mCommonParamsMap);
                // ApiManager.this.executor.cancel(api.getRequestKey()); //干掉以前的请求
                ApiManager.this.executor.execute(api, new AbsApi.IApiResultDispatcher<T>() {
                    @Override
                    public void onResponseOK(T data, AbsApi api) {
                        if (api != null) {
                            if (api.isStatusOK()) {
                                e.onNext(data);
                                e.onComplete();
                            }
                        } else {
                            e.onError(null);
                        }
                    }

                    @Override
                    public void onResponseError(Object o, AbsApi api, Throwable throwable) {
                        e.onError(throwable);
                    }
                }, priority);
            }
        }, BackpressureStrategy.BUFFER).toObservable();
    }

    /**
     * 取消请求
     *
     * @param api
     */
    public void cancel(AbsApi api) {
        if (api == null) {
            return;
        }
        if (executor == null) {
            return;
        }
        executor.cancel(api.getRequestKey());
    }
}
