package com.violet.net.sports.request.bean;

import android.content.Context;

import com.android.volley.Request;
import com.violet.net.sports.request.annotation.JsonReaderClass;

import java.util.Collections;
import java.util.Map;

/**
 * Created by kan212 on 2018/7/27.
 */
@JsonReaderClass
public abstract class BaseHttpBean extends BaseSportsBean{

    /**
     * 当前请求绑定的page号（在可支持翻页请求的列表加载情况下，主要用于缓存读取顺序逻辑），默认为0。
     */
    public int mBindPageIndex;

    /**
     * 请求类型（默认为Request.Method.POST）
     *
     * @return
     */
    public int getHttpMethod() {
        return Request.Method.POST;
    }

    /**
     * 请求地址
     *
     * @param context
     * @return
     */
    public abstract String getURL(Context context);

    /**
     * 请求参数
     *
     * @return
     */
    public abstract Map<String, String> getParams();

    /**
     * 获取响应序号
     *
     * @return
     */
    public abstract int getResponseCode();

    /**
     * 获取响应反馈信息
     *
     * @return
     */
    public abstract String getResponseMsg();

    /**
     * 获取header
     * @return
     */
    public Map<String, String> getHeader() {
        return Collections.emptyMap();
    }
}
