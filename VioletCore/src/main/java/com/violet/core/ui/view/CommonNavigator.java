package com.violet.core.ui.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.violet.core.R;
import com.violet.core.ui.view.indicator.PositionData;
import com.violet.core.ui.view.indicator.adapter.CommonNavigatorAdapter;
import com.violet.core.ui.view.indicator.helper.NavigatorHelper;
import com.violet.core.ui.view.indicator.inter.IMeasurablePagerTitleView;
import com.violet.core.ui.view.indicator.inter.IPagerIndicator;
import com.violet.core.ui.view.indicator.inter.IPagerNavigator;
import com.violet.core.ui.view.indicator.inter.IPagerTitleView;
import com.violet.core.ui.view.indicator.inter.ScrollState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kan212 on 2018/4/14.
 */

public class CommonNavigator extends FrameLayout implements IPagerNavigator, NavigatorHelper.OnNavigatorScrollListener {

    private NavigatorHelper mNavigatorHelper;
    private HorizontalScrollView mScrollView;
    private LinearLayout mTitleContainer;
    private LinearLayout mIndicatorContainer;
    private IPagerIndicator mIndicator;

    private CommonNavigatorAdapter mAdapter;

    /**
     * 提供给外部的参数配置
     */
    /****************************************************/
    private boolean mAdjustMode;   // 自适应模式，适用于数目固定的、少量的title
    private int mRightPadding;
    private int mLeftPadding;
    private boolean mIndicatorOnTop;    // 指示器是否在title上层，默认为下层
    private boolean mFollowTouch = true;    // 是否手指跟随滚动
    private float mScrollPivotX = 0.5f; // 滚动中心点 0.0f - 1.0f
    private boolean mEnablePivotScroll; // 启动中心点滚动

    // 保存每个title的位置信息，为扩展indicator提供保障
    private List<PositionData> mPositionDataList = new ArrayList<PositionData>();
    private boolean mReselectWhenLayout = true; // PositionData准备好时，是否重新选中当前页，为true可保证在极端情况下指示器状态正确
    private boolean mSmoothScroll = true;   // 是否平滑滚动，适用于 !mAdjustMode && !mFollowTouch

    public CommonNavigator(@NonNull Context context) {
        super(context);
        mNavigatorHelper = new NavigatorHelper();
        mNavigatorHelper.setNavigatorScrollListener(this);
    }

    public CommonNavigator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonNavigator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(CommonNavigatorAdapter adapter) {
        if (mAdapter == adapter) {
            return;
        }
        if (null != mAdapter) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;
        if (null != mAdapter) {
            mAdapter.registerDataSetObserver(mObserver);
            mNavigatorHelper.setTotalCount(mAdapter.getCount());
            // adapter改变时，应该重新init，但是第一次设置adapter不用，onAttachToMagicIndicator中有init
            if (mTitleContainer != null) {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mNavigatorHelper.setTotalCount(0);
            init();
        }
    }

    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mNavigatorHelper.setTotalCount(mAdapter.getCount());    // 如果使用helper，应始终保证helper中的totalCount为最新
            init();
        }

        @Override
        public void onInvalidated() {
//            super.onInvalidated();
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (null != mAdapter) {
            mNavigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if (null != mIndicator) {
                mIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // 手指跟随滚动
            if (mScrollView != null && mPositionDataList.size() > 0 && position >= 0 && position < mPositionDataList.size()) {
                if (mFollowTouch) {
                    int currentPosition = Math.min(mPositionDataList.size() - 1, position);
                    int nextPosition = Math.min(mPositionDataList.size() - 1, position + 1);
                    PositionData current = mPositionDataList.get(currentPosition);
                    PositionData next = mPositionDataList.get(nextPosition);
                    float scrollTo = current.horizontalCenter() - mScrollView.getWidth() * mScrollPivotX;
                    float nextScrollTo = next.horizontalCenter() - mScrollView.getWidth() * mScrollPivotX;
                    mScrollView.scrollTo((int) (scrollTo + (nextScrollTo - scrollTo) * positionOffset), 0);
                } else if (!mEnablePivotScroll) {
                    // TODO 实现待选中项完全显示出来

                }
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mAdapter != null) {
            mNavigatorHelper.onPageSelected(position);
            if (mIndicator != null) {
                mIndicator.onPageSelected(position);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (null != mAdapter) {
            mNavigatorHelper.onPageScrollStateChanged(state);
            if (null != mIndicator) {
                mIndicator.onPageScrollStateChanged(state);
            }
        }
    }

    @Override
    public void onAttachToMagicIndicator() {
        init();
    }

    @Override
    public void onDetachFromMagicIndicator() {

    }

    private void init() {
        removeAllViews();
        View root;
        if (mAdjustMode) {
            root = LayoutInflater.from(getContext()).inflate(R.layout.navigator_layout_no_scroll, this);
        } else {
            root = LayoutInflater.from(getContext()).inflate(R.layout.navigator_layout, this);
        }
        mScrollView = (HorizontalScrollView) root.findViewById(R.id.scroll_view);   // mAdjustMode为true时，mScrollView为null

        mTitleContainer = (LinearLayout) root.findViewById(R.id.title_container);
        mTitleContainer.setPadding(mLeftPadding, 0, mRightPadding, 0);

        mIndicatorContainer = (LinearLayout) root.findViewById(R.id.indicator_container);
        if (mIndicatorOnTop) {
            mIndicatorContainer.getParent().bringChildToFront(mIndicatorContainer);
        }
        initTitlesAndIndicator();
    }

    /**
     * 初始化title和indicator
     */
    private void initTitlesAndIndicator() {
        for (int i = 0, j = mNavigatorHelper.getTotalCount(); i < j; i++) {
            IPagerTitleView v = mAdapter.getTitleView(getContext(), i);
            if (v instanceof View) {
                View view = (View) v;
                LinearLayout.LayoutParams lp;
                if (mAdjustMode) {
                    lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.weight = mAdapter.getTitleWeight(getContext(), i);
                } else {
                    lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                }
                mTitleContainer.addView(view, lp);
            }
        }
        if (mAdapter != null) {
            mIndicator = mAdapter.getIndicator(getContext());
            if (mIndicator instanceof View) {
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mIndicatorContainer.addView((View) mIndicator, lp);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAdapter != null) {
            preparePositionData();
            if (mIndicator != null) {
                mIndicator.onPositionDataProvide(mPositionDataList);
            }
            if (mReselectWhenLayout && mNavigatorHelper.getScrollState() == ScrollState.SCROLL_STATE_IDLE) {
                onPageSelected(mNavigatorHelper.getCurrentIndex());
                onPageScrolled(mNavigatorHelper.getCurrentIndex(), 0.0f, 0);
            }
        }
    }

    /**
     * 获取title的位置信息，为打造不同的指示器、各种效果提供可能
     */
    private void preparePositionData() {
        mPositionDataList.clear();
        for (int i = 0, j = mNavigatorHelper.getTotalCount(); i < j; i++) {
            PositionData data = new PositionData();
            View v = mTitleContainer.getChildAt(i);
            if (v != null) {
                data.mLeft = v.getLeft();
                data.mTop = v.getTop();
                data.mRight = v.getRight();
                data.mBottom = v.getBottom();
                if (v instanceof IMeasurablePagerTitleView) {
                    IMeasurablePagerTitleView view = (IMeasurablePagerTitleView) v;
                    data.mContentLeft = view.getContentLeft();
                    data.mContentTop = view.getContentTop();
                    data.mContentRight = view.getContentRight();
                    data.mContentBottom = view.getContentBottom();
                } else {
                    data.mContentLeft = data.mLeft;
                    data.mContentTop = data.mTop;
                    data.mContentRight = data.mRight;
                    data.mContentBottom = data.mBottom;
                }
            }
            mPositionDataList.add(data);
        }
    }

    public void setAdjustMode(boolean is) {
        mAdjustMode = is;
    }


    @Override
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onEnter(index, totalCount, enterPercent, leftToRight);
        }
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onLeave(index, totalCount, leavePercent, leftToRight);
        }
    }

    @Override
    public void onSelected(int index, int totalCount) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onSelected(index, totalCount);
        }
        if (!mAdjustMode && !mFollowTouch && mScrollView != null && mPositionDataList.size() > 0) {
            int currentIndex = Math.min(mPositionDataList.size() - 1, index);
            PositionData current = mPositionDataList.get(currentIndex);
            if (mEnablePivotScroll) {
                float scrollTo = current.horizontalCenter() - mScrollView.getWidth() * mScrollPivotX;
                if (mSmoothScroll) {
                    mScrollView.smoothScrollTo((int) (scrollTo), 0);
                } else {
                    mScrollView.scrollTo((int) (scrollTo), 0);
                }
            } else {
                // 如果当前项被部分遮挡，则滚动显示完全
                if (mScrollView.getScrollX() > current.mLeft) {
                    if (mSmoothScroll) {
                        mScrollView.smoothScrollTo(current.mLeft, 0);
                    } else {
                        mScrollView.scrollTo(current.mLeft, 0);
                    }
                } else if (mScrollView.getScrollX() + getWidth() < current.mRight) {
                    if (mSmoothScroll) {
                        mScrollView.smoothScrollTo(current.mRight - getWidth(), 0);
                    } else {
                        mScrollView.scrollTo(current.mRight - getWidth(), 0);
                    }
                }
            }
        }
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        if (mTitleContainer == null) {
            return;
        }
        View v = mTitleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onDeselected(index, totalCount);
        }
    }
}
