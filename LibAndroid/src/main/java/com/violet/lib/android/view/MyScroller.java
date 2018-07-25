package com.violet.lib.android.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by kan212 on 2018/6/4.
 */

public class MyScroller extends Scroller{

    public MyScroller(Context context) {
        super(context);
    }

    public MyScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public MyScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }
}
