package com.violet.base.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kan212 on 2018/6/11.
 */

public abstract class BaseObjectAdapter<T> extends BaseAdapter {

    protected final String TAG;

    protected LayoutInflater mInflater;
    protected Resources mResources;

    protected List<T> mDataList = new ArrayList<T>();// 数据集合

    public BaseObjectAdapter(Context context) {
        TAG = "ADAPTER_" + getClass().getSimpleName();

        mInflater = LayoutInflater.from(context);
        mResources = context.getResources();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return (position >= 0 && position < mDataList.size()) ? mDataList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 添加单条数据
     *
     * @param obj
     */
    public boolean add(T obj) {
        if (null != obj) {
            return mDataList.add(obj);
        }
        return false;
    }

    /**
     * 添加单条数据到指定位置
     *
     * @param index
     * @param obj
     */
    public void add(int index, T obj) {
        if (null != obj && index >= 0 && index <= mDataList.size()) {
            mDataList.add(index, obj);
        }
    }

    /**
     * 添加列表数据
     *
     * @param collection
     * @return
     */
    public boolean addAll(List<T> collection) {
        if (null != collection && collection.size() > 0) {
            return mDataList.addAll(collection);
        }
        return false;
    }

    /**
     * 添加列表数据到指定位置
     *
     * @param index
     * @param collection
     * @return
     */
    public boolean addAll(int index, List<T> collection) {
        if (null != collection && collection.size() > 0) {
            return mDataList.addAll(index, collection);
        }
        return false;
    }

    /**
     * 添加数组数据
     *
     * @param array
     * @return
     */
    public void addAll(T[] array) {
        if (null != array && array.length > 0) {
            for (int i = 0, size = array.length; i < size; i++) {
                mDataList.add(array[i]);
            }
        }
    }

    /**
     * 移除指定位置的数据
     *
     * @param index
     * @return
     */
    public T remove(int index) {
        if (index >= 0 && index < mDataList.size()) {
            return mDataList.remove(index);
        }
        return null;
    }

    /**
     * 移除特定数据
     *
     * @param obj
     * @return
     */
    public boolean remove(T obj) {
        if (null != obj) {
            return mDataList.remove(obj);
        }
        return false;
    }

    /**
     * 清空数据
     */
    public void clear() {
        mDataList.clear();
    }

    /**
     * 重置数据
     *
     * @param collection
     */
    public synchronized void reset(List<T> collection) {
        if (null != collection && collection.size() > 0) {
            mDataList.clear();
            mDataList.addAll(collection);
        }
    }

}
