package com.violet.base.ui.view.layoutmanager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.ref.WeakReference;


public class CoverInsertPostLayoutListener extends CarouselZoomPostLayoutListener {

    private static WeakReference<View> topView;
    private static int layoutCount;
    private static int sAdapterPosition;
    private static final int INVALID_POSITION = -1;

    private static boolean isAdapterValid() {
        return sAdapterPosition != INVALID_POSITION;
    }

    public static void reset() {
        setTopView(null, INVALID_POSITION);
    }

    private static void setTopView(@Nullable View view, int adapterPos) {
        if (view == null) {
            topView = null;
            sAdapterPosition = adapterPos;
            return;
        }
        topView = new WeakReference<>(view);
        sAdapterPosition = adapterPos;
    }

    @Nullable
    private static View getTopView() {
        if (topView == null) {
            return null;
        }
        return topView.get();
    }

    private static void setLayoutCount(int totalCount) {
        layoutCount = totalCount;
    }

    private static boolean isLayoutCountChange(int totalCount) {
        if (totalCount != layoutCount) {
            setLayoutCount(totalCount);
            return true;
        }
        return false;
    }


    @SuppressWarnings("RedundantIfStatement")
    private static boolean isCoverCard(View view, float diff) {
        if (view != getTopView()) {
            return false;
        }
        final float absDiff = Math.abs(diff);
        if (absDiff <= 1f && absDiff >= 0f) {
            return true;
        }
        return false;
    }

    @Override
    public ItemTransformation transformChild(@NonNull View child, float itemPositionToCenterDiff, int orientation, int index, int totalCount, int adapterPosition) {
        final ItemTransformation transformation = super.transformChild(child, itemPositionToCenterDiff, orientation, index, totalCount, adapterPosition);
        if (orientation == CarouselLayoutManager.VERTICAL) {
            return transformation;
        }
        if (getTopView() != null && isLayoutCountChange(totalCount)) {
            setTopView(null, INVALID_POSITION);
        }
        setLayoutCount(totalCount);
        if (getTopView() == null && index == totalCount - 1) {
            setTopView(child, adapterPosition);
        }
        if (isCoverCard(child, itemPositionToCenterDiff)) {
            coverInsert(child, itemPositionToCenterDiff, transformation, totalCount);
        }
        if (coverInsertFinish(child, itemPositionToCenterDiff, index, totalCount, adapterPosition)) {
            setTopView(null, INVALID_POSITION);
        }
        // 计算第二层View移动的距离
        return transformation;
    }

    /**
     * 判断回插是否结束
     */
    protected boolean coverInsertFinish(@NonNull View child, float itemPositionToCenterDiff, int index, int totalCount, int adapterPosition) {
        return (child == getTopView()
                && (Math.abs(itemPositionToCenterDiff) >= 1 || itemPositionToCenterDiff == 0))
                || coverViewInVisible(index, totalCount, adapterPosition);
    }

    /**
     * 检查CoverView是否可见, 通过比较Adapter中的位置实现
     *
     * @return true 当前捕获的TopView不可见, 需要清除/ otherwise false.
     */
    protected boolean coverViewInVisible(int index, int totalCount, int adapterPos) {
        if (!isAdapterValid()) {
            return false;
        }
        final boolean isTopView = index == totalCount - 1;
        final int range = totalCount - 1;
        if (totalCount == 5 || totalCount == 3) {
            final boolean coverViewInVisible = (sAdapterPosition < adapterPos - range
                    || sAdapterPosition > adapterPos + 1);
            final boolean notBound = sAdapterPosition != 0 && adapterPos != 0;
            return isTopView && coverViewInVisible && notBound;
        }
        return false;
    }

    /**
     * 顶部卡片的回插效果
     */
    protected void coverInsert(@NonNull View child, float itemPositionToCenterDiff, ItemTransformation transformation, int layoutCount) {
        final int childWidth = child.getMeasuredWidth();
        final float absDiff = Math.abs(itemPositionToCenterDiff);
        final float offsetBound = (childWidth) * (2.1f);
        if (absDiff > .5f) {
            final float signum = Math.signum(itemPositionToCenterDiff);
            transformation.mTranslationX = signum * .5f * offsetBound - (itemPositionToCenterDiff - signum * .5f) * offsetBound;
        } else if (absDiff > 0f) {
            transformation.mTranslationX = itemPositionToCenterDiff * offsetBound;
        }
    }
}
