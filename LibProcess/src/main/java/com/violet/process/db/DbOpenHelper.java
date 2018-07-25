package com.violet.process.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kan212 on 2018/4/18.
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "process.db";
    public final static String TABLE_NAME = "process";
    private final static int DB_VERSION = 1;
    private final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + "(_id integer primary key, name TEXT, description TEXT)";

    public DbOpenHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
