package com.violet.core.sqilte;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kan212 on 2018/7/30.
 */

public class DBManager {

    public static final String COLUMN_AUTO_TIME = "auto_time";// 自动添加时间字段
    private static DBManager sInstance = null;
    private static final Object lock = new Object();
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private int execCount = 0;

    public DBManager(Context context){
        dbHelper = new DBHelper(context);
    }

    public static DBManager getInstance(Context context){
        if (null == sInstance){
            synchronized (lock){
                if (null == sInstance){
                    sInstance = new DBManager(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化数据库
     * @return
     */
    public SQLiteDatabase getSQLiteDatabase(){
        if (null == database){
            database = dbHelper.getWritableDatabase();
            execCount = 0;
        }
        return database;
    }


}
