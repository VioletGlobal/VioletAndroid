package com.violet.core.ui.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * Created by kan212 on 2018/4/12.
 */

public class SwipeBackLayout extends ViewGroup {

    public enum DragDirectMode {
        EDGE,
        VERTICAL,
        HORIZONTAL
    }

    public enum DragEdge {
        LEFT,

        TOP,

        RIGHT,

        BOTTOM
    }

    public SwipeBackLayout(Context context) {
        this(context, null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);
        chkDragable();
    }

    private DragEdge dragEdge = DragEdge.TOP;
    private DragDirectMode dragDirectMode = DragDirectMode.EDGE;
    private SwipeBackListener swipeBackListener;
    /**
     * Whether allow to pull this layout.
     */
    private boolean enablePullToBack = true;

    private final ViewDragHelper viewDragHelper;

    private View target;

    private View scrollChild;

    private int verticalDragRange = 0;

    private int horizontalDragRange = 0;

    private static final float BACK_FACTOR = 0.5f;

    private int draggingState = 0;

    private int draggingOffset;

    /**
     * the anchor of calling finish.
     */
    private float finishAnchor = 0;

    private boolean enableFlingBack = true;

    private static final double AUTO_FINISHED_SPEED_LIMIT = 2000.0;

    public void setDragEdge(DragEdge dragEdge) {
        this.dragEdge = dragEdge;
    }

    public void setDragDirectMode(DragDirectMode dragDirectMode) {
        this.dragDirectMode = dragDirectMode;
        if (dragDirectMode == DragDirectMode.VERTICAL) {
            this.dragEdge = DragEdge.TOP;
        } else if (dragDirectMode == DragDirectMode.HORIZONTAL) {
            this.dragEdge = DragEdge.LEFT;
        }
    }

    public void setOnSwipeBackListener(SwipeBackListener listener) {
        swipeBackListener = listener;
    }

    public void setEnablePullToBack(boolean b) {
        enablePullToBack = b;
    }

    float lastY = 0;
    float newY = 0;
    float offsetY = 0;

    float lastX = 0;
    float newX = 0;
    float offsetX = 0;

    private void chkDragable() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    newY = event.getRawY();
                    lastX = event.getRawX();

                    offsetY = Math.abs(newY - lastY);
                    lastY = newY;

                    offsetX = Math.abs(newX - lastX);
                    lastX = newX;

                    switch (dragEdge) {
                        case TOP:
                        case BOTTOM:
                            setEnablePullToBack(offsetY > offsetX);
                        case LEFT:
                        case RIGHT:
                            setEnablePullToBack(offsetY < offsetX);
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handled = false;
        ensureTarget();
        if (isEnabled()) {
            /** 是否应该拦截 children 的触摸事件，
             *只有拦截了 ViewDragHelper 才能进行后续的动作
             *将它放在 ViewGroup 中的 onInterceptTouchEvent() 方法中就好了
             **/
            handled = viewDragHelper.shouldInterceptTouchEvent(ev);
        } else {
            viewDragHelper.cancel();
        }
        return !handled ? super.onInterceptTouchEvent(ev) : handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            /**
             * 处理 ViewGroup 中传递过来的触摸事件序列
             * 在ViewGroup 中的 onTouchEvent() 方法中处理
             */
            viewDragHelper.processTouchEvent(event);
        }
        return isEnabled();
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void ensureTarget() {
        if (null == target) {
            if (getChildCount() > 1) {
                throw new IllegalStateException("SwipeBackLayout must contains only one direct child");
            }
            target = getChildAt(0);
            if (null == scrollChild && null != target) {
                if (target instanceof ViewGroup) {
                    findScrollView((ViewGroup) target);
                } else {
                    scrollChild = target;
                }
            }
        }
    }

    /**
     * Find out the scrollable child view
     *
     * @param viewGroup
     */
    private void findScrollView(ViewGroup viewGroup) {
        scrollChild = viewGroup;
        if (viewGroup.getChildCount() > 0) {
            int count = viewGroup.getChildCount();
            View child;
            for (int i = 0; i < count; i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof AbsListView || child instanceof ScrollView || child instanceof ViewPager || child instanceof WebView) {
                    scrollChild = child;
                    return;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        View child = getChildAt(0);
        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingTop() - getPaddingBottom();
        int childLeft = getPaddingLeft();
        int childRight = childLeft + childWidth;
        int childTop = getPaddingTop();
        int childBottom = childHeight + childTop;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        verticalDragRange = h;
        horizontalDragRange = w;
        switch (dragEdge) {
            case TOP:
            case BOTTOM:
                finishAnchor = finishAnchor > 0 ? finishAnchor : verticalDragRange * BACK_FACTOR;
                break;
            case LEFT:
            case RIGHT:
                finishAnchor = finishAnchor > 0 ? finishAnchor : horizontalDragRange * BACK_FACTOR;
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 1) {
            throw new IllegalStateException("SwipeBackLayout must contains only one direct child");
        }
        if (getChildCount() > 0) {
            int measureWidth = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
            int measureHeight = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
            getChildAt(0).measure(measureWidth, measureHeight);
        }
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {

        // 决定了是否需要捕获这个 child，只有捕获了才能进行下面的拖拽行为
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return false;
        }

        //边缘拖拽开始
        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            if (null != target) {
                if (edgeFlags == ViewDragHelper.EDGE_BOTTOM
                        || edgeFlags == ViewDragHelper.EDGE_LEFT
                        || edgeFlags == ViewDragHelper.EDGE_RIGHT
                        || edgeFlags == ViewDragHelper.EDGE_TOP) {
                    //指定被拖动的 child
                    viewDragHelper.captureChildView(target, pointerId);
                }
            }
        }

        //大于0的时候用来移动 clickable == true 的控件
        @Override
        public int getViewVerticalDragRange(View child) {
            return verticalDragRange;
        }

        //大于0的时候用来移动 clickable == true 的控件
        @Override
        public int getViewHorizontalDragRange(View child) {
            return horizontalDragRange;
        }

        // 修整 child 水平方向上的坐标，left 指 child 要移动到的坐标，dx 相对上次的偏移量
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int result = 0;

            if (dragDirectMode == DragDirectMode.HORIZONTAL) {
                if (!canChildScrollRight() && left > 0) {
                    dragEdge = DragEdge.LEFT;
                } else if (!canChildScrollLeft() && left < 0) {
                    dragEdge = DragEdge.RIGHT;
                }
            }

            if (dragEdge == DragEdge.LEFT && !canChildScrollRight() && left > 0) {
                final int leftBound = getPaddingLeft();
                final int rightBound = horizontalDragRange;
                result = Math.min(Math.max(left, leftBound), rightBound);
            } else if (dragEdge == DragEdge.RIGHT && !canChildScrollLeft() && left < 0) {
                final int leftBound = -horizontalDragRange;
                final int rightBound = getPaddingLeft();
                result = Math.min(Math.max(left, leftBound), rightBound);
            }

            return result;
        }

        // 修整 child 垂直方向上的坐标，top 指 child 要移动到的坐标，dy 相对上次的偏移量
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int result = 0;
            if (dragDirectMode == DragDirectMode.VERTICAL) {
                if (!canChildScrollUp() && top > 0) {
                    dragEdge = DragEdge.TOP;
                } else if (!canChildScrollDown() && top < 0) {
                    dragEdge = DragEdge.BOTTOM;
                }
            }

            if (dragEdge == DragEdge.TOP && !canChildScrollUp() && top > 0) {
                final int topBound = getPaddingTop();
                final int bottomBound = verticalDragRange;
                result = Math.min(Math.max(top, topBound), bottomBound);
            } else if (dragEdge == DragEdge.BOTTOM && !canChildScrollDown() && top < 0) {
                final int topBound = -verticalDragRange;
                final int bottomBound = getPaddingTop();
                result = Math.min(Math.max(top, topBound), bottomBound);
            }
            return result;
        }

        ////当托拽状态变化时回调，譬如动画结束后回调为STATE_IDLE等
        @Override
        public void onViewDragStateChanged(int state) {
            if (state == draggingState){
                return;
            }
            if ((draggingState == ViewDragHelper.STATE_DRAGGING || draggingState == ViewDragHelper.STATE_SETTLING)
                    && state == ViewDragHelper.STATE_IDLE){
                if (draggingOffset == getDragRange()){
                    finish();
                }
            }
            draggingState = state;
        }
        //当前被触摸的View位置变化时回调
        //changedView为位置变化的View，left/top变化时新的x左/y顶坐标，dx/dy为从旧到新的偏移量
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            switch (dragEdge) {
                case TOP:
                case BOTTOM:
                    draggingOffset = Math.abs(top);
                    break;
                case LEFT:
                case RIGHT:
                    draggingOffset = Math.abs(left);
                    break;
                default:
                    break;
            }
            float fractionAnchor = draggingOffset / finishAnchor;
            if (fractionAnchor >= 1){
                fractionAnchor = 1;
            }
            float fractionScreen = draggingOffset / getDragRange();
            if (fractionScreen >= 1){
                fractionAnchor = 1;
            }
            if (null != swipeBackListener){
                swipeBackListener.onViewPositionChanged(fractionAnchor,fractionScreen);
            }
        }

        // 手指释放时的回调
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (draggingOffset == 0){
                return;
            }
            if (draggingOffset == getDragRange()){
                return;
            }
            boolean isBack = false;

            if (enableFlingBack && backBySpeed(xvel, yvel)) {
                isBack = !canChildScrollUp();
            } else if (draggingOffset >= finishAnchor) {
                isBack = true;
            } else if (draggingOffset < finishAnchor) {
                isBack = false;
            }

            int finalLeft;
            int finalTop;
            switch (dragEdge) {
                case LEFT:
                    finalLeft = isBack ? horizontalDragRange : 0;
                    smoothScrollToX(finalLeft);
                    break;
                case RIGHT:
                    finalLeft = isBack ? -horizontalDragRange : 0;
                    smoothScrollToX(finalLeft);
                    break;
                case TOP:
                    finalTop = isBack ? verticalDragRange : 0;
                    smoothScrollToY(finalTop);
                    break;
                case BOTTOM:
                    finalTop = isBack ? -verticalDragRange : 0;
                    smoothScrollToY(finalTop);
                    break;
            }
        }
    }

    private void smoothScrollToX(int finalLeft) {
        //将 child 安置到坐标 (finalLeft,finalTop) 的位置
        if (viewDragHelper.settleCapturedViewAt(finalLeft, 0)) {
            ViewCompat.postInvalidateOnAnimation(SwipeBackLayout.this);
        }
    }

    private void smoothScrollToY(int finalTop) {
        if (viewDragHelper.settleCapturedViewAt(0, finalTop)) {
            ViewCompat.postInvalidateOnAnimation(SwipeBackLayout.this);
        }
    }

    private int getDragRange() {
        switch (dragEdge) {
            case TOP:
            case BOTTOM:
                return verticalDragRange;
            case LEFT:
            case RIGHT:
                return horizontalDragRange;
            default:
                return verticalDragRange;
        }
    }

    private boolean backBySpeed(float xvel, float yvel) {
        switch (dragEdge) {
            case TOP:
            case BOTTOM:
                if (Math.abs(yvel) > Math.abs(xvel) && Math.abs(yvel) > AUTO_FINISHED_SPEED_LIMIT) {
                    return dragEdge == DragEdge.TOP ? !canChildScrollUp() : !canChildScrollDown();
                }
                break;
            case LEFT:
            case RIGHT:
                if (Math.abs(xvel) > Math.abs(yvel) && Math.abs(xvel) > AUTO_FINISHED_SPEED_LIMIT) {
                    return dragEdge == DragEdge.LEFT ? !canChildScrollLeft() : !canChildScrollRight();
                }
                break;
        }
        return false;
    }

    private void finish() {
        Activity act = (Activity) getContext();
        act.finish();
        act.overridePendingTransition(0, android.R.anim.fade_out);
    }

    //可以向上滑动
    public boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(scrollChild, -1);
    }

    //可以向下滑动
    public boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(scrollChild, 1);
    }

    //可以向右滑动
    private boolean canChildScrollRight() {
        return ViewCompat.canScrollHorizontally(scrollChild, -1);
    }

    //可以向左滑动
    private boolean canChildScrollLeft() {
        return ViewCompat.canScrollHorizontally(scrollChild, 1);
    }

    public interface SwipeBackListener {

        /**
         * Return scrolled fraction of the layout.
         *
         * @param fractionAnchor relative to the anchor.
         * @param fractionScreen relative to the screen.
         */
        void onViewPositionChanged(float fractionAnchor, float fractionScreen);

    }
}
