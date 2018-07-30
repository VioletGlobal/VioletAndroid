package com.violet.lib.feed.sports.feed.cache;

import android.content.Context;

import com.violet.core.sqilte.DBManager;
import com.violet.lib.feed.sports.feed.bean.SportsFeedBean;
import com.violet.net.sports.request.cache.VolleyCacher;

import java.util.ArrayList;

/**
 * Created by kan212 on 2018/7/30.
 */

public class FeedDBCacher extends VolleyCacher<SportsFeedBean>{

    private static final long CACHE_VALID_TIME = 10 * 60 * 1000;// 缓存有效时间

    protected DBManager mDBManager;
    protected SQLSentenceCallbackForNewsTab callbackForNewsTab;

    @Override
    public void init(Context context, String url) {
        super.init(context, url);
    }

    @Override
    public void doCache(byte[] data, SportsFeedBean bean) {
        super.doCache(data, bean);
    }

    @Override
    public ArrayList<SportsFeedBean> readCache() {
        return super.readCache();
    }

    @Override
    public void loadCache(OnCacheLoadListener<SportsFeedBean> listener) {
        super.loadCache(listener);
    }

    @Override
    public boolean isCacheExpiration() {
        return super.isCacheExpiration();
    }

    @Override
    public String getNameForSqlTableAndCacheDir() {
        return super.getNameForSqlTableAndCacheDir();
    }
}
