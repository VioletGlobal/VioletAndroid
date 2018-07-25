package com.violet.core.ui.view.indicator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.violet.core.ui.view.indicator.inter.IPagerNavigator;

/**
 * Created by kan212 on 2018/4/12.
 */

public class MagicIndicator extends FrameLayout{

    private IPagerNavigator mNavigator;

    public MagicIndicator(@NonNull Context context) {
        super(context);
    }

    public MagicIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MagicIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mNavigator != null) {
            mNavigator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        if (mNavigator != null) {
            mNavigator.onPageSelected(position);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (mNavigator != null) {
            mNavigator.onPageScrollStateChanged(state);
        }
    }

    public IPagerNavigator getNavigator() {
        return mNavigator;
    }

    public void setNavigator(IPagerNavigator navigator) {
        if (mNavigator == navigator) {
            return;
        }
        if (mNavigator != null) {
            mNavigator.onDetachFromMagicIndicator();
        }
        mNavigator = navigator;
        removeAllViews();
        if (mNavigator instanceof View) {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.bottomMargin = 0;
            addView((View) mNavigator, lp);
            mNavigator.onAttachToMagicIndicator();
        }
    }
}
