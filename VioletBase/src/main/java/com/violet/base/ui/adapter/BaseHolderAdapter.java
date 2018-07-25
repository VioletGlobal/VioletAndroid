package com.violet.base.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.violet.base.bean.BaseHolderBean;
import com.violet.base.inter.OnViewHolderCallbackListener;

import java.util.LinkedList;

/**
 * Created by kan212 on 2018/6/11.
 */

public abstract class BaseHolderAdapter<D extends BaseHolderBean> extends BaseScrollMonitorAdapter<D> {

    private LinkedList<String> mViewTypeList = new LinkedList<String>();
    private OnViewHolderCallbackListener mViewHolderCallbackListener;

    public BaseHolderAdapter(Context context) {
        super(context);
        getTypeViewHolders(mViewTypeList);
    }

    /**
     * 获取ViewHolder配置类
     *
     * @return
     */
    public abstract Class<?> getConfigClass();

    /**
     * View创建时，传入viewHolder.onViewCreated()中的bundle对象
     *
     * @param viewHolderTag
     * @param position
     * @return
     */
    protected Bundle getViewHolderCreatedBundle(String viewHolderTag, int position) {
        return null;
    }

    /**
     * View创建时，传入viewHolder.onViewCreated()中的bundle对象
     *
     * @param viewHolderTag
     * @param bean
     * @param position
     * @return
     */
    protected Bundle getViewHolderShowBundle(String viewHolderTag, D bean, int position) {
        return null;
    }

    @Override
    public D getItem(int position) {
        return (position >= 0 && position < getDataList().size()) ? getDataList().get(position) : null;
    }

    /**
     * 获取ViewHolder类型配置
     *
     * @param viewTypeList
     * @return
     * @值 String表示当前View的构建标签（由ConfigClass进行维护）
     */
    protected abstract void getTypeViewHolders(LinkedList<String> viewTypeList);


    @Override
    public int getViewTypeCount() {
        return mViewTypeList.size();
    }

    /**
     * 根据源数据判定当前ViewHolder类型
     *
     * @param sourceData
     * @return
     */
    protected abstract String getItemViewHolderTag(D sourceData);

    /**
     * 获取指定项的ViewHolder标签
     *
     * @param position
     * @return
     */
    public String getItemViewHolderTag(int position) {
        if (position >= 0 && position < getCount() && getViewTypeCount() > 0) {
            D itemData = getItem(position);
            if (null != itemData) {
                return getItemViewHolderTag(itemData);
            }
        }
        return "";
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= 0 && position < getCount() && getViewTypeCount() > 0) {
            D itemData = getItem(position);
            if(null != itemData){
                String viewHolderTag = getItemViewHolderTag(itemData);
                return mViewTypeList.indexOf(viewHolderTag);
            }
        }
        return -1;
    }

    /**
     * 将来指定位置源数据转换成ViewHolder数据
     *
     * @param <B>
     * @param viewHolderTag
     * @param sourceBean
     * @return
     */
    public abstract <B extends BaseHolderBean> B transform(String viewHolderTag, D sourceBean);

    /**
     * 设置ViewHolder通信回调方法
     *
     * @param listener
     */
    public void setViewHolderCallbackListener(OnViewHolderCallbackListener listener) {
        mViewHolderCallbackListener = listener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
