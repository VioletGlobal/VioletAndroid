package com.violet.process.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.violet.process.db.DbOpenHelper;

import static android.content.UriMatcher.NO_MATCH;

/**
 * Created by kan212 on 2018/4/18.
 * <p>
 * authority：整个提供程序的符号名称
 * path：指向表的名称/路径
 */

public class IPCProcessProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = new UriMatcher(NO_MATCH);
    public static final String AUTHORITY = "com.violet.provider.IPCProcessProvider";  //授权
    public static final Uri PROCESS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/process");
    private static final String TAG = "IPCProcessProvider: ";

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private String mTable;

    private static final int TABLE_CODE_PROCESS = 2;

    static {
        //关联不同的 URI 和 code，便于后续 getType
        mUriMatcher.addURI(AUTHORITY, "process", TABLE_CODE_PROCESS);
    }


    //初始化 provider
    //默认执行在主线程，别做耗时操作
    @Override
    public boolean onCreate() {
        initProvider();
        return false;
    }

    private void initProvider() {
        mTable = DbOpenHelper.TABLE_NAME;
        mContext = getContext();
        mDatabase = new DbOpenHelper(mContext).getWritableDatabase();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDatabase.execSQL("delete from " + mTable);
                mDatabase.execSQL("insert into " + mTable + " values(1,'process','fuck the world')");
            }
        }).start();
    }

    private String getTableName(final Uri uri) {
        String tabName = "";
        int match = mUriMatcher.match(uri);
        switch (match) {
            case TABLE_CODE_PROCESS:
                tabName = DbOpenHelper.TABLE_NAME;
                break;
        }
        return tabName;
    }

    //查询数据
    //最好异步操作
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String tableName = getTableName(uri);
        Log.d(TAG, "查询数据: " + tableName);
        return mDatabase.query(tableName, projection, selection, selectionArgs, null, sortOrder, null);
    }

    //返回 provider 中的数据的 MIME 类型
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    //插入数据到 provider
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String tableName = getTableName(uri);
        Log.d(TAG, "插入数据: " + tableName);
        mDatabase.insert(tableName, null, values);
        mContext.getContentResolver().notifyChange(uri, null);
        return null;
    }

    //删除 provider 中的数据
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        Log.d(TAG, "删除数据: " + tableName);
        int deleteCount = mDatabase.delete(tableName, selection, selectionArgs);
        if (deleteCount > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return deleteCount;
    }

    //更新 provider 的数据
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        Log.d(TAG, "更新数据: " + tableName);
        int updateCount = mDatabase.update(tableName, values, selection, selectionArgs);
        if (updateCount > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }
}
