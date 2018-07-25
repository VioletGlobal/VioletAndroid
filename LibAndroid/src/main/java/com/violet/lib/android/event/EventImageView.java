package com.violet.lib.android.event;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.violet.core.util.LogUtil;

/**
 * Created by kan212 on 2018/6/5.
 */

public class EventImageView extends android.support.v7.widget.AppCompatImageView{

    public EventImageView(Context context) {
        super(context);
    }

    public EventImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EventImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogUtil.d("EventImageView dispatchTouchEvent"+ event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d("EventImageView onTouchEvent"+ event.getAction());
        return super.onTouchEvent(event);
    }
}
