package com.violet.net.sports.request.jsonreader;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.violet.net.sports.request.VolleyRequestWithFinishListener;
import com.violet.net.sports.request.bean.BaseHttpBean;
import com.violet.net.sports.request.cache.VolleyCacher;
import com.violet.net.sports.request.imp.VolleyResponseListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kan212 on 2018/7/27.
 */

public class VolleyRequest<T extends BaseHttpBean> extends VolleyRequestWithFinishListener<VolleyResponseParser<T>> {

    private WeakReference<Context> contextWeakReference;
    private T mBean;
    private VolleyCacher<T> mVolleyCacher;
    private VolleyResponseListener<T> mVolleyResponseListener;

    public VolleyRequest(Context context, T bean, int method, String url, VolleyCacher<T> volleyCacher, VolleyResponseListener<T> volleyResponseListener) {
        super(method, url, null);
        // 废弃Volley原有缓存逻辑
        setShouldCache(false);
        // 全局变量初始化
        contextWeakReference = new WeakReference<Context>(context);
        mBean = bean;
        mVolleyCacher = volleyCacher;
        mVolleyResponseListener = volleyResponseListener;
    }

    @Override
    protected Response<VolleyResponseParser<T>> parseNetworkResponse(NetworkResponse response) {
        if(null != response){
            VolleyResponseParser<T> responseParser = new VolleyResponseParser<T>();
            String charset = HttpHeaderParser.parseCharset(response.headers, "UTF-8");// 默认编码格式为：UTF-8
            responseParser.mNetworkTimeMs = response.networkTimeMs;
            responseParser.mNotModified = response.notModified;
            responseParser.mStatusCode = response.statusCode;

            try {
                responseParser.parserByJsonReader(mBean.getClass(), response.data, charset);
                // 设置当前请求的page编号，用于缓存存储
                if (null != responseParser.mBean) {
                    responseParser.mBean.mBindPageIndex = mBean.mBindPageIndex;
                }
                if (null != mVolleyCacher && null != contextWeakReference.get()) {
                    // 在请求成功，并数据解析成功后，添加缓存
                    mVolleyCacher.doCache(response.data, responseParser.mBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Response.error(new ParseError(e));
            } finally {
                mBean = null;
            }
            return Response.success(responseParser,HttpHeaderParser.parseCacheHeaders(response));
        }
        return Response.error(new ParseError(new Exception("response error")));
    }

    @Override
    protected void deliverResponse(VolleyResponseParser<T> response) {
        if (null != response) {
            mResponseListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        if (null != mErrorListener) {
            mErrorListener.onErrorResponse(error);
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return getTotalParams();
    }

    /**
     * 获取全部的请求参数
     *
     * @return
     */
    public Map<String, String> getTotalParams() {
        Map<String, String> params = mBean.getParams();
        if (null != mVolleyResponseListener) {
            if (null == params) {
                params = new HashMap<String, String>(0);
            }
            mVolleyResponseListener.configParams(params);
        }
        return params;
    }

    private final Response.Listener<VolleyResponseParser<T>> mResponseListener = new Response.Listener<VolleyResponseParser<T>>() {
        @Override
        public void onResponse(VolleyResponseParser<T> response) {
            if (null != mVolleyResponseListener) {
                String failMsg = "";
                if (null != response) {
                    if (response.mStatusCode >= 200 && response.mStatusCode < 300 || 304 == response.mStatusCode) {
                        // HTTP请求成功
                        if (null != response.mBean) {
                            if (0 == response.mBean.getResponseCode()) {
                                mVolleyResponseListener.onSuccessResponse(contextWeakReference.get(), response.mBean);
                            } else {
                                mVolleyResponseListener.onFailResponse(contextWeakReference.get(), response.mBean.getResponseMsg());
                            }
                            return;
                        } else {
                            // 响应数据为空
                            failMsg = "data is null";
                        }
                    } else {
                        // HTTP反馈错误
                        failMsg = "fail code is " + response.mStatusCode;
                    }
                } else {
                    // HTTP响应为空
                    failMsg = "response is null";
                }
                mVolleyResponseListener.onFailResponse(contextWeakReference.get(), failMsg);
            }
        }
    };

    private final Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (null != mVolleyResponseListener) {
                mVolleyResponseListener.onErrorResponse(contextWeakReference.get(), error);
            }
        }
    };

}
