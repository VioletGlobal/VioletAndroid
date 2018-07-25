package com.violet.core.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by kan212 on 2018/6/12.
 */

public abstract class CoreViewGroup extends ViewGroup {

    public CoreViewGroup(Context context) {
        super(context);
    }

    public CoreViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoreViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * boolean consume = false;
     * 父View决定是否拦截事件
     * if(onInterceptTouchEvent(event)){
     *  父View调用onTouchEvent(event)消费事件，如果该方法返回true，表示
     *  该View消费了该事件，后续该事件序列的事件（Down、Move、Up）将不会在传递
     *  该其他View。
     *      consume = onTouchEvent(event);
     *  }else{
     *      调用子View的dispatchTouchEvent(event)方法继续分发事件
     *      consume = child.dispatchTouchEvent(event);
     * }
     * return consume;
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
