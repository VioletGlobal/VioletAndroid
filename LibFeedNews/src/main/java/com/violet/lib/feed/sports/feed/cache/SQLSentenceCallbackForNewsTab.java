package com.violet.lib.feed.sports.feed.cache;

import android.database.Cursor;

import com.violet.core.sqilte.DBManager;
import com.violet.core.sqilte.SQLSentenceCallback;
import com.violet.lib.feed.sports.feed.bean.NewsDataItemBean;

/**
 * Created by kan212 on 2018/7/30.
 */

public class SQLSentenceCallbackForNewsTab implements SQLSentenceCallback<NewsDataItemBean> {

    public static final String FEED = "feed";
    public static final String FOCUS = "focus";
    public static final String SPECIAL = "special";
    private String mTableName;

    public void setTableName(String tableName) {
        mTableName = tableName;
    }

    @Override
    public String getTableName() {
        return mTableName;
    }

    @Override
    public String onCreateSQL() {
        return null;
    }

    @Override
    public String onUpgradeSQL() {
        return null;
    }

    @Override
    public String isExistSQL(NewsDataItemBean data) {
        return null;
    }

    @Override
    public String insertSQL(NewsDataItemBean item) {
        return null;
    }

    @Override
    public String querySQL(String... args) {
        return null;
    }

    @Override
    public NewsDataItemBean decodeCursor(Cursor cursor) {
        return null;
    }

    @Override
    public String updateSQL(NewsDataItemBean older, NewsDataItemBean newer) {
        return null;
    }

    @Override
    public String deleteSQL(String... args) {
        return null;
    }

    @Override
    public long getTableCreatedTime(DBManager manager) {
        return 0;
    }

    @Override
    public long getTableLastChangedTime(DBManager manager) {
        return 0;
    }
}
