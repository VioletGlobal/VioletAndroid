package com.violet.base.ui.view.base;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kan212 on 2018/4/20.
 */

public abstract class BaseView extends View {

    private BaseScroller mBaseScroller;

    public BaseView(Context context) {
        this(context,null);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBaseScroller = new BaseScroller(context);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mBaseScroller.computeScrollOffset()){
            scrollTo(mBaseScroller.getCurrX(),mBaseScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 该方法会递归调用父窗口的requestLayout()方法，直到触发ViewRootImpl的performTraversals()方法，
     * 此时mLayoutRequestede为true，会触发onMesaure()与onLayout()方法，不一定 会触发onDraw()方法。
     */
    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    /**
     * 该方法递归调用父View的invalidateChildInParent()方法，直到调用ViewRootImpl的invalidateChildInParent()方法，
     * 最终触发ViewRootImpl的performTraversals()方法，此时mLayoutRequestede为false，不会 触发onMesaure()与onLayout()方法，
     * 当时会触发onDraw()方法
     */
    @Override
    public void invalidate() {
        super.invalidate();
    }
}
