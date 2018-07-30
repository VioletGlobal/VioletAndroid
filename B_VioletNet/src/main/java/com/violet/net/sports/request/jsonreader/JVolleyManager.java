package com.violet.net.sports.request.jsonreader;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.violet.net.sports.request.VolleyManager;
import com.violet.net.sports.request.bean.BaseHttpBean;
import com.violet.net.sports.request.cache.VolleyCacher;
import com.violet.net.sports.request.imp.VolleyResponseListener;
import com.violet.net.sports.request.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by kan212 on 2018/7/27.
 */

public class JVolleyManager {

    public static <T extends BaseHttpBean> VolleyRequest<T> add(
            Context context,
            Class<T> className,
            VolleyResponseListener<T> listener) {
        return add(context, className, null, false, false, 0, null, listener);
    }


    public static <T extends BaseHttpBean> VolleyRequest<T> add(
            Context context,
            Class<T> className,
            String url,
            boolean isSupportCDNService,
            boolean isUseOwnCache,
            final int pageIndex,
            VolleyCacher<T> ownCache,
            final VolleyResponseListener<T> listener) {
        if (null == context || null == className) {
            return null;
        }
        try {
            // 创建当前HttpBean对象
            T bean = className.newInstance();
            // 设置当前请求的绑定page编号
            bean.mBindPageIndex = pageIndex;
            // 根据RequestMethod获取当前请求的URL
            String fullURL = "";
            if (Request.Method.GET == bean.getHttpMethod()){
                // http Get方式拼接URL
                Map<String, String> params = bean.getParams();
                if (null == params) {
                    params = new HashMap<String, String>();
                }
                if (null != listener) {
                    listener.configParams(params);
                }
                if (TextUtils.isEmpty(url)) {
                    fullURL = Util.dealGetURL(bean.getURL(context), params);
                } else {
                    fullURL = Util.dealGetURL(url, params);
                }
            }else {
                fullURL = bean.getURL(context);
            }

            // 初始化缓存对象
            if (null != ownCache) {
                ownCache.init(context, fullURL);
            }
            //创建请求
            final VolleyRequest<T> request = new VolleyRequest<T>(context, bean, bean.getHttpMethod(), fullURL, ownCache, listener);
            if (null != ownCache && isUseOwnCache){
                ownCache.loadCache(new VolleyCacher.OnCacheLoadListener<T>() {
                    @Override
                    public void cacheLoadOver(Context context, boolean isExpiration, ArrayList<T> httpBean) {
                        if (null != context){
                            // 首次请求，优先加载缓存
                            if (0 == pageIndex && null != httpBean && httpBean.size() > 0) {
                                // 进行缓存数据输出
                                for (int i = 0, s = httpBean.size(); i < s; i++) {
                                    T bean = httpBean.get(i);
                                    if (null != bean && null != listener) {
                                        listener.onSuccessResponse(context, bean);
                                    }
                                }
                                boolean isShouldHttpRequst = false;// 表示是否需要进行网络重新加载
                                if (isExpiration){
                                    isShouldHttpRequst = true;
                                }else if (null == httpBean || httpBean.size() == 0){
                                    isShouldHttpRequst = true;
                                }
                                if (isShouldHttpRequst){
                                    VolleyManager.INSTANCE.addRequest(context,request);
                                }else {
                                    // 读取缓存来获取数据情况下，设置当前request的状态为finished。
                                    request.setFinished(true);
                                }
                            }
                        }
                    }
                });
            }else {
                VolleyManager.INSTANCE.addRequest(context,request);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
