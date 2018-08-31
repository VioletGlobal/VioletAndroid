package com.violet.imageloader.core;

import android.graphics.Bitmap;

import com.violet.imageloader.core.assist.LoadedFrom;
import com.violet.imageloader.core.display.BitmapDisplayer;
import com.violet.imageloader.core.imageAware.ImageAware;
import com.violet.imageloader.core.listener.ImageLoadingListener;
import com.violet.imageloader.utils.L;

/**
 * Created by kan212 on 2018/8/29.
 * 显示图片的Task，实现了Runnable接口，必须在主线程调用。
 */

public class DisplayBitmapTask implements Runnable {

    private static final String LOG_DISPLAY_IMAGE_IN_IMAGEAWARE = "Display image in ImageAware (loaded from %1$s) [%2$s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";

    private final Bitmap bitmap;
    private final String imageUri;
    private final ImageAware imageAware;
    private final String memoryCacheKey;
    private final BitmapDisplayer displayer;
    private final ImageLoadingListener listener;
    private final ImageLoaderEngine engine;
    private final LoadedFrom loadedFrom;

    public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, ImageLoaderEngine engine,
                             LoadedFrom loadedFrom) {
        this.bitmap = bitmap;
        imageUri = imageLoadingInfo.uri;
        imageAware = imageLoadingInfo.imageAware;
        memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        displayer = imageLoadingInfo.options.getDisplayer();
        listener = imageLoadingInfo.listener;
        this.engine = engine;
        this.loadedFrom = loadedFrom;
    }

    @Override
    public void run() {
        //首先判断imageAware是否被 GC 回收，如果是直接调用取消加载回调接口ImageLoadingListener.onLoadingCancelled(…)；
        if (imageAware.isCollected()){
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED, memoryCacheKey);
            listener.onLoadingCancelled(imageUri, imageAware.getWrappedView());
        }
        //判断imageAware是否被复用，如果是直接调用取消加载回调接口ImageLoadingListener.onLoadingCancelled(…)；
        else if(isViewWasReused()){
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_REUSED, memoryCacheKey);
            listener.onLoadingCancelled(imageUri,imageAware.getWrappedView());
        }
        //调用displayer显示图片，并将imageAware从正在加载的 map 中移除。调用加载成功回调接口
        else{
            L.d(LOG_DISPLAY_IMAGE_IN_IMAGEAWARE, loadedFrom, memoryCacheKey);
            displayer.display(bitmap,imageAware,loadedFrom);
            engine.cancelDisplayTaskFor(imageAware);
            listener.onLoadingComplete(imageUri, imageAware.getWrappedView(), bitmap);
        }
        //对于 ListView 或是 GridView 这类会缓存 Item 的 View 来说，单个 Item 中如果含有 ImageView，在滑动过程中可能因为异步加载及 View
        // 复用导致图片错乱，这里对imageAware是否被复用的判断就能很好的解决这个问题。
    }

    /** Checks whether memory cache key (image URI) for current ImageAware is actual */
    private boolean isViewWasReused() {
        String currentCacheKey = engine.getLoadingUriForView(imageAware);
        return !memoryCacheKey.equals(currentCacheKey);
    }
}
