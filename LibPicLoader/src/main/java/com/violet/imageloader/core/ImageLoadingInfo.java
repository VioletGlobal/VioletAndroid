package com.violet.imageloader.core;

import com.violet.imageloader.core.assist.ImageSize;
import com.violet.imageloader.core.imageAware.ImageAware;
import com.violet.imageloader.core.listener.ImageLoadingListener;
import com.violet.imageloader.core.listener.ImageLoadingProgressListener;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kan212 on 2018/8/29.
 * 加载和显示图片任务需要的信息。
 */

final class ImageLoadingInfo {

    //图片 url
    final String uri;
    //图片缓存 key。
    final String memoryCacheKey;
    //需要加载图片的对象。
    final ImageAware imageAware;
    //图片的显示尺寸。
    final ImageSize targetSize;
    //图片显示的配置项。
    final DisplayImageOptions options;
    //图片加载各种时刻的回调接口。
    final ImageLoadingListener listener;
    //图片加载进度的回调接口。
    final ImageLoadingProgressListener progressListener;
    // 图片加载中的重入锁。
    final ReentrantLock loadFromUriLock;

    public ImageLoadingInfo(String uri, ImageAware imageAware, ImageSize targetSize, String memoryCacheKey,
                            DisplayImageOptions options, ImageLoadingListener listener,
                            ImageLoadingProgressListener progressListener, ReentrantLock loadFromUriLock) {
        this.uri = uri;
        this.imageAware = imageAware;
        this.targetSize = targetSize;
        this.options = options;
        this.listener = listener;
        this.progressListener = progressListener;
        this.loadFromUriLock = loadFromUriLock;
        this.memoryCacheKey = memoryCacheKey;
    }
}
