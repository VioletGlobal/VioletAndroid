package com.violet.core.sqilte;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kan212 on 2018/7/30.
 */

public class DBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "violet.db";
    public static final int DATABASE_VERSION = 1;
    SQLSentenceCallback mSQLSentenceCallback;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 设置SQL语句回调
     *
     * @param callback
     */
    public void setSQLSentenceCallback(SQLSentenceCallback callback) {
        mSQLSentenceCallback = callback;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * 查询所有表
     * #link https://blog.csdn.net/sloan6/article/details/49640539
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()){
            String name = cursor.getString(0);
            db.execSQL("drop table if exists" + name);
        }
        cursor.close();
    }
}
