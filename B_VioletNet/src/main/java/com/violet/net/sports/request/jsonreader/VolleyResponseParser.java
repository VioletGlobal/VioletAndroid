package com.violet.net.sports.request.jsonreader;

import com.violet.net.sports.request.bean.BaseHttpBean;

/**
 * Created by kan212 on 2018/7/27.
 */

public class VolleyResponseParser<T extends BaseHttpBean> {

    private final String TAG;

    public int mStatusCode;// HTTP返回状态码
    public boolean mNotModified;// True if the server returned a 304 (Not Modified)
    public long mNetworkTimeMs;// 网络请求耗时（毫秒）

    public T mBean;

    public VolleyResponseParser() {
        TAG = getClass().getSimpleName();
    }

    public void parserByJsonReader(Class<? extends BaseHttpBean> aClass, byte[] data, String charset) {
    }
}
