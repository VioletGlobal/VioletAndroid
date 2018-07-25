package com.violet.base.ui.view.layoutmanager;

import android.support.annotation.NonNull;
import android.view.View;

public class ThreeCoverInsertPostLayoutListener extends CarouselZoomPostLayoutListener {

    private static final int LAYER3_EXPAND = 50;

    @Override
    public ItemTransformation transformChild(@NonNull View child, float itemPositionToCenterDiff, int orientation, int index, int totalCount, int adapterPosition) {
        ItemTransformation transformation = super.transformChild(child, itemPositionToCenterDiff, orientation, index, totalCount, adapterPosition);
        // 如果是第三层的View， 调整View的摆放位置
        expandOutside(transformation, index);

        return transformation;
    }

    @Override
    protected float getScale() {
        return SCALE;
    }

    /**
     * 调整各层级卡片边界
     */
    private void expandOutside(ItemTransformation transformation, int index) {
        int layer3Expand = LAYER3_EXPAND;

        final int expandBound3 = layer3Expand * 5 / 3;
        if (index == 0) {
            transformation.mTranslationX -= expandBound3;
        }
        if (index == 1) {
            transformation.mTranslationX += expandBound3;
        }

    }

}
