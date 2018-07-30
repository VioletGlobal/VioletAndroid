package com.violet.net.sports.request.cache;

import android.content.Context;
import android.os.AsyncTask;

import com.violet.net.sports.request.bean.BaseHttpBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by kan212 on 2018/7/27.
 */

public abstract class VolleyCacher<T extends BaseHttpBean> {

    private boolean isInited = false;// 表示当前VolleyCacher是否初始化过
    protected WeakReference<Context> mWeakReferenceContext;
    protected String mUrl = "";

    protected String mNameForSqlTableAndCacheDir = "";// 数据库表和本地缓存文件夹的名称
    private CacheLoadTask cacheLoadTask;

    public  void init(Context context, String url){
        mWeakReferenceContext = new WeakReference<Context>(context);
        mUrl = url;
        // 获取当前缓存的数据库表名和本地文件夹名称
        mNameForSqlTableAndCacheDir = getNameForSqlTableAndCacheDir();
        // 初始化完成
        isInited = true;
    }

    /**
     * 读取缓存（耗时操作，需要在子线程中进行）
     */
    public ArrayList<T> readCache() {
        if (!isInited) {
            try {
                throw new Exception("VolleyCacher is not init, please excute init() first");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 子线程加载缓存
     *
     * @param listener 缓存加载监听
     */
    public void loadCache(OnCacheLoadListener<T> listener) {
        if (!isInited) {
            try {
                throw new Exception("VolleyCacher is not init, please excute init() first");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (null != cacheLoadTask) {
            cacheLoadTask.cancel(true);
            cacheLoadTask = null;
        }
        cacheLoadTask = new CacheLoadTask(mWeakReferenceContext, listener);
        cacheLoadTask.execute();
    }

    /**
     * 缓存处理(在Volley请求线程中处理)
     *
     * @param data 二进制数据，可直接存入文件
     * @param bean 对象数据，可进行序列号存储
     */
    public void doCache(byte[] data, T bean) {
        if (!isInited) {
            try {
                throw new Exception("VolleyCacher is not init, please excute init() first");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 缓存是否过期
     *
     * @return true表示缓存过期
     */
    public boolean isCacheExpiration() {
        if (!isInited) {
            try {
                throw new Exception("VolleyCacher is not init, please excute init() first");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 获取数据库表和本地缓存文件夹的名称
     *
     * @return
     * @tip Sqlite数据库表名不能已数字开头
     */
    public String getNameForSqlTableAndCacheDir() {
        return "";
    }

    /**
     * 缓存过期检测结果监听器
     */
    public interface OnCacheLoadListener<T extends BaseHttpBean> {

        /**
         * 缓存过期检测回馈
         *
         * @param context      上下文
         * @param isExpiration 缓存是否过期
         * @param httpBeanList 缓存结果列表
         */
        public void cacheLoadOver(Context context, boolean isExpiration, ArrayList<T> httpBeanList);
    }

    /**
     * 缓存读取结果对象类
     */
    private class CacheLoadResultBean {
        boolean isCacheExpiration = true;
        ArrayList<T> bean;
    }

    class CacheLoadTask extends AsyncTask<String,Void,CacheLoadResultBean>{

        WeakReference<Context> contextWeakReference;
        OnCacheLoadListener<T> mCacheLoadListener;

        CacheLoadTask(WeakReference<Context> context, OnCacheLoadListener<T> cacheLoadListener) {
            contextWeakReference = context;
            mCacheLoadListener = cacheLoadListener;
        }
        @Override
        protected CacheLoadResultBean doInBackground(String... strings) {
            CacheLoadResultBean result = new CacheLoadResultBean();
            result.isCacheExpiration = isCacheExpiration();
            result.bean = readCache();
            return result;
        }

        @Override
        protected void onPostExecute(CacheLoadResultBean result) {
            // 缓存读取反馈
            if (null != mCacheLoadListener) {
                mCacheLoadListener.cacheLoadOver(contextWeakReference.get(), result.isCacheExpiration, result.bean);
            }
        }
    }
}
