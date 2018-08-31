package com.violet.imageloader.core;

import android.graphics.Bitmap;
import android.os.Handler;

import com.violet.imageloader.core.assist.LoadedFrom;
import com.violet.imageloader.core.process.BitmapProcessor;
import com.violet.imageloader.utils.L;

/**
 * Created by kan212 on 2018/8/30.
 * 处理并显示图片的Task，实现了Runnable接口。
 */

public class ProcessAndDisplayImageTask implements Runnable{


    private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";

    private final ImageLoaderEngine engine;
    private final Bitmap bitmap;
    private final ImageLoadingInfo imageLoadingInfo;
    private final Handler handler;

    public ProcessAndDisplayImageTask(ImageLoaderEngine engine, Bitmap bitmap, ImageLoadingInfo imageLoadingInfo,
                                      Handler handler) {
        this.engine = engine;
        this.bitmap = bitmap;
        this.imageLoadingInfo = imageLoadingInfo;
        this.handler = handler;
    }

    /**
     * 主要通过 imageLoadingInfo 得到BitmapProcessor处理图片，并用处理后的图片和配置新建一个DisplayBitmapTask在ImageAware中显示图片。
     */
    @Override
    public void run() {
        L.d(LOG_POSTPROCESS_IMAGE, imageLoadingInfo.memoryCacheKey);
        BitmapProcessor processor = imageLoadingInfo.options.getPostProcessor();
        Bitmap procssBitmap = processor.process(bitmap);
        DisplayBitmapTask task = new DisplayBitmapTask(procssBitmap,imageLoadingInfo,engine, LoadedFrom.MEMORY_CACHE);
        LoadAndDisplayImageTask.runTask(task,imageLoadingInfo.options.isSyncLoading(),handler,engine);
    }
}
