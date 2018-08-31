package com.violet.imageloader.core.imageAware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.violet.imageloader.core.assist.ViewScaleType;

/**
 * Created by kan212 on 2018/8/29.
 */

public interface ImageAware {

    int getWidth();

    int getHeight();

    ViewScaleType getScaleType();

    View getWrappedView();

    boolean isCollected();

    int getId();

    boolean setImageDrawable(Drawable drawable);

    boolean setImageBitmap(Bitmap bitmap);

}
