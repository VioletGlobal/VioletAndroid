package com.violet.lib.opensource.ultra;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.violet.lib.opensource.R;
import com.violet.lib.opensource.ultra.indicator.PtrIndicator;

/**
 * Created by kan212 on 2018/9/3.
 * UltraPTR 的核心类，自定义控件类。
 */

public class PtrFrameLayout extends ViewGroup {

    public final static byte PTR_STATUS_INIT = 1;
    private byte mStatus = PTR_STATUS_INIT;
    public final static byte PTR_STATUS_PREPARE = 2;
    public final static byte PTR_STATUS_LOADING = 3;
    public final static byte PTR_STATUS_COMPLETE = 4;
    private static final boolean DEBUG_LAYOUT = true;

    private static int ID = 1;
    protected final String LOG_TAG = "ptr-frame-" + ++ID;
    // auto refresh status
    private final static byte FLAG_AUTO_REFRESH_AT_ONCE = 0x01;
    private final static byte FLAG_AUTO_REFRESH_BUT_LATER = 0x01 << 1;
    private final static byte FLAG_ENABLE_NEXT_PTR_AT_ONCE = 0x01 << 2;
    private final static byte FLAG_PIN_CONTENT = 0x01 << 3;
    private final static byte MASK_AUTO_REFRESH = 0x03;
    protected View mContent;
    // optional config for define header and content in xml file
    private int mHeaderId = 0;
    private int mContainerId = 0;
    // config
    private int mDurationToClose = 200;
    private int mDurationToCloseHeader = 1000;
    private boolean mKeepHeaderWhenRefresh = true;
    private boolean mPullToRefresh = false;
    private View mHeaderView;
    private PtrUIHandlerHolder mPtrUIHandlerHolder = PtrUIHandlerHolder.create();
    private PtrHandler mPtrHandler;
    // working parameters
    private ScrollChecker mScrollChecker;
    private int mPagingTouchSlop;
    private int mHeaderHeight;
    private boolean mDisableWhenHorizontalMove = false;
    private int mFlag = 0x00;

    // disable when detect moving horizontally
    private boolean mPreventForHorizontal = false;

    private MotionEvent mLastMoveEvent;

    private PtrUIHandlerHook mRefreshCompleteHook;

    private int mLoadingMinTime = 500;
    private long mLoadingStartTime = 0;
    private PtrIndicator mPtrIndicator;
    private boolean mHasSendCancelEvent = false;

    private Runnable mPerformRefreshCompleteDelay = new Runnable() {
        @Override
        public void run() {
            performRefreshComplete();
        }
    };

    public PtrFrameLayout(Context context) {
        this(context, null);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPtrIndicator = new PtrIndicator();
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PtrFrameLayout, 0, 0);
        if (arr != null) {

            mHeaderId = arr.getResourceId(R.styleable.PtrFrameLayout_ptr_header, mHeaderId);
            mContainerId = arr.getResourceId(R.styleable.PtrFrameLayout_ptr_content, mContainerId);

            mPtrIndicator.setResistance(
                    arr.getFloat(R.styleable.PtrFrameLayout_ptr_resistance, mPtrIndicator.getResistance()));

            mDurationToClose = arr.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close, mDurationToClose);
            mDurationToCloseHeader = arr.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close_header, mDurationToCloseHeader);

            float ratio = mPtrIndicator.getRatioOfHeaderToHeightRefresh();
            ratio = arr.getFloat(R.styleable.PtrFrameLayout_ptr_ratio_of_header_height_to_refresh, ratio);
            mPtrIndicator.setRatioOfHeaderHeightToRefresh(ratio);

            mKeepHeaderWhenRefresh = arr.getBoolean(R.styleable.PtrFrameLayout_ptr_keep_header_when_refresh, mKeepHeaderWhenRefresh);

            mPullToRefresh = arr.getBoolean(R.styleable.PtrFrameLayout_ptr_pull_to_fresh, mPullToRefresh);
            arr.recycle();
        }
        mScrollChecker = new ScrollChecker();
    }

    /**
     * view的所有子控件被xml映射之后调用
     */
    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("PtrFrameLayout can only contains 2 children");
        } else if (childCount == 2) {
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
            }
            if (mContainerId != 0 && mContent == null) {
                mContent = findViewById(mContainerId);
            }

            if (mContent == null || mHeaderView == null) {
                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof PtrUIHandler) {
                    mHeaderView = child1;
                    mContent = child2;
                } else if (child2 instanceof PtrUIHandler) {
                    mHeaderView = child2;
                    mContent = child1;
                } else {
                    if (mContent == null && mHeaderView == null) {
                        mHeaderView = child1;
                        mContent = child2;
                    } else {
                        if (mHeaderView == null) {
                            mHeaderView = mContent == child1 ? child2 : child1;
                        } else {
                            mContent = mHeaderView == child1 ? child2 : child1;
                        }
                    }
                }
            }
        } else if (childCount == 1) {
            mContent = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            mContent = errorView;
            addView(mContent);
        }
        //Change the view's z order in the tree, so it's on top of other sibling views
        if (mHeaderView != null) {
            mHeaderView.bringToFront();
        }
        super.onFinishInflate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mScrollChecker) {
            mScrollChecker.destroy();
        }
        if (mPerformRefreshCompleteDelay != null) {
            removeCallbacks(mPerformRefreshCompleteDelay);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (null != mHeaderView) {
            /**
             *  measure 时考虑把 margin 及  padding 也作为子视图大小的一部分
             *  #link https://blog.csdn.net/abwbw/article/details/40185627
             */
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mPtrIndicator.setHeaderHeight(mHeaderHeight);
        }

        if (null != mContent) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);
        }

    }

    /**
     * #link https://www.jianshu.com/p/75a775213654
     * 测量子View大小，测量出指定的MeasureSpec 给一个单独的子View，这个方法要计算出子View正确的HeightMeasureSpec或者WidthMeasureSpec
     */
    private void measureContentView(View mContent, int widthMeasureSpec, int heightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
        /**
         * 流程:
         * 1、从父View的HeightMeasureSpec中获取specMode和specSize
         * 2、算出size为Math.max(0，specSize-padding)，算出来的值就是当前父View还剩的空间大小
         * 3、判断父View中的SpecMode
         * 4、如果specMode为MeasureSpec.EXACTLY：意味着父View强制设置了一个大小给子View：
         * a) 判断childDimension>=0，如果满足，则将size设置成childDimension，Mode设置成MeasureSpec.EXACTLY，
         * 意味着如果XML中设置了具体的大小的话，那么就使用XML中具体的指定的大小
         * b) 判断childDimension是否为LayoutParams.MATCH_PARENT，则将size设置成父View的size，Mode设置为MeasureSpec.EXACTLY
         * ，意味着建议子View的大小被强行设置成父View的大小
         * c) 判断childDimension是否为LayoutParams.WRAP_CONTENT，则将size设置成父View的size，Mode设置为MeasureSpec.AT_MOST，
         * 意味着让子View去处理，子View最大的大小不能超过父View
         * 5、如果SpecMode为MeasureSpec.AT_MOST的话：意味着父View给子View一个限定的大小，子View不能超过这个大小
         * a) 判断childDimension>=0，如果满足，则将子View的size设置成XML中配置的大小，并且将 Mode设置成MeasureSpec.EXACTLY，
         * 意味着View的大小就是XML中设置的大小
         * b) 如果childDimension为LayoutParams.MATCH_PARENT，则子View的大小为父View的size，但是Mode则为MeasureSpec.AT_MOST，
         * 说明希望子View的大小不要超过父View的大小
         * c) 如果childDimension为LayoutParams.WRAP_CONTENT，则与b)一样
         * 6、如果SpecMode为MeasureSpec.UNSPECIFIED的话：意味着子View要多大都可以
         * a) 判断childDimension>=0，如果是的话，那么则将size设置成childDimension，而mode设置成MeasureSpec.EXACTLY，
         * 意味着，我可以给让你想显示多大就显示多大，但是在XML中或者addView的时候， 写死了childDimension，那么它的大小就这么大吧
         * b) 判断childDimension为LayoutParams.MATCH_PARENT或者是LayoutParams.WRAP_CONTENT，则将size设置为0，然后mode设置为MeasureSpec.UNSPECIFIED，
         * 意味着大小让它自己决定到底要多大，随便多大都可以
         *
         */
        final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                getPaddingBottom() + getPaddingTop() + lp.topMargin + lp.bottomMargin, lp.height);
        mContent.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled() || mContent == null || mHeaderView == null) {
            return dispatchTouchEventSupper(ev);
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mHasSendCancelEvent = false;
                mPtrIndicator.onPressDown(ev.getX(), ev.getY());
                //按下时候停止之前的
                mScrollChecker.abortIfWorking();
                mPreventForHorizontal = false;
                //the cancel event will be sent once the position is moved.so let the event pass to children
                dispatchTouchEventSupper(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = ev;
                mPtrIndicator.onMove(ev.getX(), ev.getY());
                float offsetX = mPtrIndicator.getOffsetX();
                //Y值的变换有比例
                float offsetY = mPtrIndicator.getOffsetY();
                //如果是横向移动的话把事件传递给子视图
                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop
                        && Math.abs(offsetX) > Math.abs(offsetY))) {
                    if (mPtrIndicator.isInStartPosition()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return dispatchTouchEventSupper(ev);
                }
                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mPtrIndicator.hasLeftStartPosition();
                //disable move when header not reach top
                if (moveDown && mPtrHandler != null && !mPtrHandler.checkCanDoRefresh(this, mContent, mHeaderView)) {
                    return dispatchTouchEventSupper(ev);
                }
                //可以下拉刷新的时候，在下拉的时候和上拉并且可以滑动的情况下移动视图
                if ((moveUp && canMoveUp) || moveDown) {
                    movePos(offsetY);
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //设置不是在点击的状态
                mPtrIndicator.onRelease();
                //如果有滑动偏移
                if (mPtrIndicator.hasLeftStartPosition()) {
                    onRelease(false);
                    if (mPtrIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSupper(ev);
                } else {
                    return dispatchTouchEventSupper(ev);
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void sendCancelEvent() {
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(),
                MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendDownEvent(){
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
    }

    private void layoutChildren() {
        //目前的y轴的滚动距离
        int offset = mPtrIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (null != mHeaderView) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            //计算 Header top 值的时候，向上偏移的一个 Header 的高度 （mHeaderHeight），这样初始情况下， Header 就会被隐藏
            final int top = -(mHeaderHeight - paddingTop - lp.topMargin - offset);
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left,top,right,bottom);
        }
        if (null != mContent){
            if (isPinContent()){
                offset = 0;
            }
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offset;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            mContent.layout(left, top, right, bottom);
        }
    }

    public boolean isAutoRefresh() {
        return (mFlag & MASK_AUTO_REFRESH) > 0;
    }

    public boolean isEnabledNextPtrAtOnce() {
        return (mFlag & FLAG_ENABLE_NEXT_PTR_AT_ONCE) > 0;
    }

    /**
     * 执行刷新结束的标识
     */
    private void performRefreshComplete() {
        mStatus = PTR_STATUS_COMPLETE;
        //如果在刷新时候，滚动还没有停止的话，先等待
        if (mScrollChecker.mIsRunning && isAutoRefresh()) {
            return;
        }
        notifyUIRefreshComplete(false);
    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public boolean isPullToRefresh() {
        return mPullToRefresh;
    }

    public int getOffsetToRefresh() {
        return mPtrIndicator.getOffsetToRefresh();
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    class ScrollChecker implements Runnable {

        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        //初始化scroller
        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            //来进行判断滚动操作是否已经完成了
            boolean finish = !mScroller.computeScrollOffset() || !mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            reset();
            onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        /**
         * 强制停止
         */
        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mPtrIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mPtrIndicator.getCurrentPosY();
            mTo = to;
            int distance = to - mStart;
            removeCallbacks(this);
            mLastFlingY = 0;
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }

        public void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }
    }

    private void onPtrScrollFinish() {
        if (mPtrIndicator.hasLeftStartPosition() && isAutoRefresh()){
            onRelease(true);
        }
    }

    /**
     * 目前看是在每次按下的时候重置状态的感觉
     */
    private void onPtrScrollAbort() {
        if (mPtrIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            onRelease(true);
        }
    }

    private void onRelease(boolean stayForLoading) {
        tryToPerformRefresh();
        if (mStatus == PTR_STATUS_LOADING) {
            if (mKeepHeaderWhenRefresh) {
                //如果是滑动过了，就滑动回到loading时候需要保持的高度
                if (mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() && !stayForLoading) {
                    mScrollChecker.tryToScrollTo(mPtrIndicator.getOffsetToKeepHeaderWhileLoading(), mDurationToClose);
                }
            } else {
                // 刷新的时候也返回到顶部
                tryScrollBackToTopWhileLoading();
            }
        } else {
            if (mStatus == PTR_STATUS_COMPLETE) {
                notifyUIRefreshComplete(false);
            } else {
                tryScrollBackToTopAbortRefresh();
            }
        }
    }

    /**
     * 真正的刷新操作
     *
     * @param ignoreHook
     */
    private void notifyUIRefreshComplete(boolean ignoreHook) {
        if (mPtrIndicator.hasLeftStartPosition() && !ignoreHook && mRefreshCompleteHook != null) {
            mRefreshCompleteHook.takeOver();
        }
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIRefreshComplete(this);
        }
        mPtrIndicator.onUIRefreshComplete();
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }

    /**
     * 通知重置状态
     */
    private boolean tryToNotifyReset() {
        //在顶部的时候同时没有在刷新的状态下重置状态
        if ((mStatus == PTR_STATUS_PREPARE || mStatus == PTR_STATUS_COMPLETE) && mPtrIndicator.isInStartPosition()) {
            if (mPtrUIHandlerHolder.hasHandler()) {
                mPtrUIHandlerHolder.onUIReset(this);
            }
            mStatus = PTR_STATUS_INIT;
            clearFlag();
            return true;
        }
        return false;
    }

    private void clearFlag() {
        mFlag = mFlag & ~MASK_AUTO_REFRESH;
    }

    public boolean isPinContent() {
        return (mFlag & FLAG_PIN_CONTENT) > 0;
    }

    /**
     * 结束时候返回到顶部
     */
    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    /**
     * 在加载的时候返回到顶部
     */
    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }

    /**
     * 刷新失败时候返回顶部
     */
    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    private void tryScrollBackToTop() {
        //不是在按下效果时候，会回到头部
        if (!mPtrIndicator.isUnderTouch()) {
            mScrollChecker.tryToScrollTo(PtrIndicator.POS_START, mDurationToCloseHeader);
        }
    }

    /**
     * 判断是不是开始
     *
     * @return
     */
    private boolean tryToPerformRefresh() {
        if (mStatus != PTR_STATUS_PREPARE) {
            return false;
        }
        if ((mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() && isAutoRefresh()) || mPtrIndicator.isOverOffsetToRefresh()) {
            mStatus = PTR_STATUS_LOADING;
            performRefresh();
        }
        return false;
    }

    /**
     * 接口回调刷新开始
     */
    private void performRefresh() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIRefreshBegin(this);
        }
        if (mPtrHandler != null) {
            mPtrHandler.onRefreshBegin(this);
        }
    }


    /**
     * 移动Y方向多少
     *
     * @param deltaY
     */
    private void movePos(float deltaY) {
        if (deltaY < 0 && mPtrIndicator.isInStartPosition()) {
            return;
        }
        int to = mPtrIndicator.getCurrentPosY() + (int) deltaY;
        if (mPtrIndicator.willOverTop(to)) {
            to = PtrIndicator.POS_START;
        }
        mPtrIndicator.setCurrentPos(to);
        int change = to - mPtrIndicator.getLastPosY();
        updatePos(change);
    }

    private void updatePos(int change) {
        if (change == 0){
            return;
        }
        boolean isUnderTouch = mPtrIndicator.isUnderTouch();
        //如果按下事件传递到下层了，还有移动
        if (isUnderTouch && !mHasSendCancelEvent && mPtrIndicator.hasMovedAfterPressedDown()){
            mHasSendCancelEvent = true;
            sendCancelEvent();
        }
        //1、已经滑动过了初始点而且现在状态是初始态
        //2、已经滚动过了刷新的最大高度，刷新态是完成，并且可以立即执行下次刷新
        if ((mPtrIndicator.hasJustLeftStartPosition() && mStatus == PTR_STATUS_INIT)
                || (mPtrIndicator.goDownCrossFinishPosition() && mStatus == PTR_STATUS_COMPLETE && isEnabledNextPtrAtOnce())){
            mStatus = PTR_STATUS_PREPARE;
            mPtrUIHandlerHolder.onUIRefreshPrepare(this);
        }
        //如果最后的位置不是起点，而当前的位置在起点
        if (mPtrIndicator.hasJustBackToStartPosition()){
            //重置状态
            tryToNotifyReset();
            //如果还是在一次点击事件中,放弃当前事件
            if (isUnderTouch){
                sendDownEvent();
            }
        }
        if(mStatus == PTR_STATUS_PREPARE){
            //滑动中，而且滑动距离超过了需要的刷新距离
            // reach fresh height while moving from top to bottom
            if (isUnderTouch && !isAutoRefresh() && mPullToRefresh && mPtrIndicator.crossRefreshLineFromTopToBottom()){
                tryToPerformRefresh();
            }
            // reach header height while auto refresh
            if (performAutoRefreshButLater() && mPtrIndicator.hasJustReachedHeaderHeightFromTopToBottom()){
                tryToPerformRefresh();
            }
        }
        mHeaderView.offsetTopAndBottom(change);
        if (!isPinContent()){
            mContent.offsetTopAndBottom(change);
        }
        invalidate();
        if (mPtrUIHandlerHolder.hasHandler()){
            mPtrUIHandlerHolder.onUIPositionChange(this,isUnderTouch,mStatus,mPtrIndicator);
        }
        onPositionChange(isUnderTouch, mStatus, mPtrIndicator);
    }

    protected void onPositionChange(boolean isInTouching, byte status, PtrIndicator mPtrIndicator) {
    }

    private boolean performAutoRefreshButLater() {
        return (mFlag & MASK_AUTO_REFRESH) == FLAG_AUTO_REFRESH_BUT_LATER;
    }


    /**
     * 初始化header
     * @param header
     */
    public void setHeaderView(View header) {
        if (mHeaderView != null && header != null && mHeaderView != header) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            header.setLayoutParams(lp);
        }
        mHeaderView = header;
        addView(header);
    }

    /**
     * 添加回调的处理holder
     * @param ptrUIHandler
     */
    public void addPtrUIHandler(PtrUIHandler ptrUIHandler) {
        PtrUIHandlerHolder.addHandler(mPtrUIHandlerHolder, ptrUIHandler);
    }
}
