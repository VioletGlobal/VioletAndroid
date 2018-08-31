package com.violet.imageloader.core.assist;

import android.widget.ImageView;

/**
 * Created by kan212 on 2018/8/29.
 * 目前看是图片缩放
 */

public enum ViewScaleType {

    FIT_INSIDE,

    CROP;

    public static ViewScaleType fromImageView(ImageView imageView) {

        switch (imageView.getScaleType()) {
            case FIT_XY:
            case FIT_CENTER:
            case FIT_END:
            case FIT_START:
            case CENTER_INSIDE:
                return FIT_INSIDE;
            case CENTER:
            case MATRIX:
            case CENTER_CROP:
            default:
                return CROP;

        }
    }
}
