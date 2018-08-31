package com.violet.imageloader.core.listener;

import android.graphics.Bitmap;
import android.view.View;

import com.violet.imageloader.core.assist.FailReason;

/**
 * Created by kan212 on 2018/8/29.
 */

public interface ImageLoadingListener {

    /**
     * 开始加载
     * @param imageUri
     * @param view
     */
    void onLoadingStarted(String imageUri, View view);

    /**
     * 加载失败
     * @param imageUri
     * @param view
     * @param failReason
     */
    void onLoadingFailed(String imageUri, View view, FailReason failReason);

    /**
     * 加载成功
     * @param imageUri
     * @param view
     * @param loadedImage
     */
    void onLoadingComplete(String imageUri, View view, Bitmap loadedImage);

    /**
     * 取消加载
     * @param imageUri
     * @param view
     */
    void onLoadingCancelled(String imageUri, View view);

}
