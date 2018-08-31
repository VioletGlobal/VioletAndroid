package com.violet.imageloader.core.listener;

import android.view.View;

/**
 * Created by kan212 on 2018/8/29.
 */

public interface ImageLoadingProgressListener {

    /**
     * 会在获取图片存储到文件系统时被回调。其中total表示图片总大小，
     * 为网络请求结果Response Header中content-length字段，如果不存在则为 -1。
     * @param imageUri
     * @param view
     * @param current
     * @param total
     */
    void onProgressUpdate(String imageUri, View view, int current, int total);
}
