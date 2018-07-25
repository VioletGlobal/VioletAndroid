package com.violet.base.ui.view.base;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.scwang.smartrefresh.layout.util.ViscousFluidInterpolator;

/**
 * Created by kan212 on 2018/6/12.
 */

public class BaseScroller extends Scroller {

    public Interpolator mInterpolator;

    public BaseScroller(Context context) {
        super(context);
    }

    public BaseScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public BaseScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
        if (interpolator == null) {
            mInterpolator = new ViscousFluidInterpolator();
        } else {
            mInterpolator = interpolator;
        }
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy);
    }
}
