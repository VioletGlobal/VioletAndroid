package com.violet.base.ui.view.layoutmanager;

import android.support.annotation.NonNull;
import android.view.View;


/**
 * Implementation of {@link CarouselLayoutManager.PostLayoutListener} that makes interesting scaling of items. <br />
 * We are trying to make items scaling quicker for closer items for center and slower for when they are far away.<br />
 * Tis implementation uses atan function for this purpose.
 * <p>
 */
public class CarouselZoomPostLayoutListener implements CarouselLayoutManager.PostLayoutListener {

    protected final static float SCALE = .15f;

    @Override
    public ItemTransformation transformChild(@NonNull final View child
            , final float itemPositionToCenterDiff, final int orientation, final int index, final int totalCount, int adapterPosition) {

        final float scale = 1 - (Math.abs(itemPositionToCenterDiff) * getScale());

        // because scaling will make view smaller in its center, then we should move this item to the top or bottom to make it visible
        // 这里仅处理缩放
        final float translateY;
        final float translateX;
        if (CarouselLayoutManager.VERTICAL == orientation) {
            translateX = 0;
            translateY = 0;
        } else {
            translateX = 0;
            translateY = 0;
        }

        return new ItemTransformation(scale, scale, translateX, translateY);
    }

    /**
     * 卡片的缩放值, 线性变化
     */
    protected float getScale() {
        return SCALE;
    }
}