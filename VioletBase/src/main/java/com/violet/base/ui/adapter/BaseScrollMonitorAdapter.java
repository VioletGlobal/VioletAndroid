package com.violet.base.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by kan212 on 2018/6/11.
 */

public abstract class BaseScrollMonitorAdapter<T> extends BaseObjectAdapter<T> {

    protected Map<Integer, Integer> mMeasureMap = new HashMap<Integer, Integer>();// viewHolder对应高度集合

    public BaseScrollMonitorAdapter(Context context) {
        super(context);
    }

    /**
     * 记录当前Adapter各项的尺寸（目前只支持高度值的记录）
     *
     * @param position
     * @param convertView
     * @param parent
     */
    protected void recordItemMeasure(int position, View convertView, ViewGroup parent) {
        int viewHeight = 0;
        if (null != convertView) {
            convertView.measure(0, 0);
            viewHeight = convertView.getMeasuredHeight();
        }
        mMeasureMap.put(position, viewHeight);
    }

    /**
     * 获取当前ListView已滑动距离
     *
     * @param firstVisiblePosition
     * @param headerViewCount
     * @param top
     * @return
     */
    public int getAdapterViewScrollHeight(int firstVisiblePosition, int headerViewCount, int top) {
        int result = 0;
        int firstVisibleAdapterViewPosition = firstVisiblePosition - headerViewCount;
        Iterator<Map.Entry<Integer, Integer>> iter = mMeasureMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, Integer> entry = iter.next();
            Integer position = entry.getKey();
            if (position < firstVisibleAdapterViewPosition) {
                int height = entry.getValue();
                result += height;
            }
        }
        result -= top;
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        mMeasureMap.clear();
    }
}
