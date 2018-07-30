package com.violet.net.sports.request.imp;

import android.content.Context;

import com.android.volley.VolleyError;
import com.violet.net.sports.request.bean.BaseHttpBean;

import java.util.Map;

/**
 * Created by kan212 on 2018/7/27.
 */

public interface VolleyResponseListener<T extends BaseHttpBean> {

    /**
     * 配置请求参数
     *
     * @param params
     */
    public void configParams(Map<String, String> params);

    /**
     * 请求成功
     *
     * @param context
     * @param response
     */
    public void onSuccessResponse(Context context, T response);

    /**
     * 请求失败
     *
     * @param context
     * @param code
     * @param failMsg
     */
    public void onFailResponse(Context context, String failMsg);

    /**
     * 请求出错
     *
     * @param context
     * @param error
     */
    public void onErrorResponse(Context context, VolleyError error);
}
