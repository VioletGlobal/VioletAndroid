package com.violet.core.sqilte;

import android.database.Cursor;

/**
 * Created by kan212 on 2018/7/30.
 */

public interface SQLSentenceCallback<T extends Object>{

    /**
     * 获取当前数据库表名称
     *
     * @return
     */
    public String getTableName();

    /**
     * 创建表
     *
     * @return
     */
    public String onCreateSQL();

    /**
     * 更新表
     *
     * @return
     */
    public String onUpgradeSQL();

    /**
     * 查看指定项是否存在
     *
     * @param data
     * @return
     */
    public String isExistSQL(T data);

    /**
     * 插入
     *
     * @param item
     * @return
     */
    public String insertSQL(T item);

    /**
     * 遍历
     *
     * @param args
     * @return
     */
    public String querySQL(String... args);

    /**
     * 解析
     *
     * @param cursor
     * @return
     */
    public T decodeCursor(Cursor cursor);

    /**
     * 更新数据
     *
     * @param older
     * @param newer
     * @return
     */
    public String updateSQL(T older, T newer);

    /**
     * 删除
     *
     * @param args
     * @return
     */
    public String deleteSQL(String... args);

    /**
     * 获取表创建时间
     *
     * @return
     */
    public long getTableCreatedTime(DBManager manager);

    /**
     * 获取表最近变化（插入、修改等）时间
     *
     * @return
     */
    public long getTableLastChangedTime(DBManager manager);

}
