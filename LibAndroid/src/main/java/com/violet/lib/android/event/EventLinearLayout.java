package com.violet.lib.android.event;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.violet.core.util.LogUtil;

/**
 * Created by kan212 on 2018/6/5.
 */

public class EventLinearLayout extends LinearLayout{
    public EventLinearLayout(Context context) {
        super(context);
    }

    public EventLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EventLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d("EventLinearLayout dispatchTouchEvent " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtil.d("EventLinearLayout onInterceptTouchEvent "+ ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d("EventLinearLayout onTouchEvent"+ event.getAction());
        return true;
    }
}
