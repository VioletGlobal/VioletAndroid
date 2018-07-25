package com.violet.core.ui.view.indicator.view;

import android.content.Context;

import com.violet.core.ui.view.indicator.ArgbEvaluatorHolder;

/**
 * 两种颜色过渡的指示器标题
 * Created by kan212 on 2018/4/16.
 */

public class ColorTransitionPagerTitleView extends SimplePagerTitleView{

    public ColorTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor);
        setTextColor(color);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor);
        setTextColor(color);
    }

    @Override
    public void onSelected(int index, int totalCount) {
    }

    @Override
    public void onDeselected(int index, int totalCount) {
    }
}
