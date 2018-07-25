package com.violet.module.main.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by kan212 on 2018/5/10.
 */

public class Rcyclerview extends RecyclerView {
    public Rcyclerview(Context context) {
        super(context);
    }

    public Rcyclerview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Rcyclerview(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return true;
    }
}
