package com.violet.imageloader.core.listener;

import android.graphics.Bitmap;
import android.view.View;

import com.violet.imageloader.core.assist.FailReason;

/**
 * Created by kan212 on 2018/8/29.
 */

public class SimpleImageLoadingListener implements ImageLoadingListener{
    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
