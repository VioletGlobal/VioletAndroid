package com.violet.imageloader.core;

import android.graphics.Bitmap;
import android.os.Handler;

import com.violet.imageloader.core.assist.FailReason;
import com.violet.imageloader.core.assist.ImageScaleType;
import com.violet.imageloader.core.assist.ImageSize;
import com.violet.imageloader.core.assist.LoadedFrom;
import com.violet.imageloader.core.assist.ViewScaleType;
import com.violet.imageloader.core.decode.ImageDecoder;
import com.violet.imageloader.core.decode.ImageDecodingInfo;
import com.violet.imageloader.core.downloader.ImageDownloader;
import com.violet.imageloader.core.imageAware.ImageAware;
import com.violet.imageloader.core.listener.ImageLoadingListener;
import com.violet.imageloader.core.listener.ImageLoadingProgressListener;
import com.violet.imageloader.utils.IoUtils;
import com.violet.imageloader.utils.L;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kan212 on 2018/8/29.
 * 加载并显示图片的Task，实现了Runnable接口，用于从网络、文件系统或内存获取图片并解析，
 * 然后调用DisplayBitmapTask在ImageAware中显示图片。
 */

final class LoadAndDisplayImageTask implements Runnable, IoUtils.CopyListener {

    private static final String LOG_WAITING_FOR_RESUME = "ImageLoader is paused. Waiting...  [%s]";
    private static final String LOG_RESUME_AFTER_PAUSE = ".. Resume loading [%s]";
    private static final String LOG_DELAY_BEFORE_LOADING = "Delay %d ms before loading...  [%s]";
    private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
    private static final String LOG_WAITING_FOR_IMAGE_LOADED = "Image already is loading. Waiting... [%s]";
    private static final String LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING = "...Get cached bitmap from memory after waiting. [%s]";
    private static final String LOG_LOAD_IMAGE_FROM_NETWORK = "Load image from network [%s]";
    private static final String LOG_LOAD_IMAGE_FROM_DISK_CACHE = "Load image from disk cache [%s]";
    private static final String LOG_RESIZE_CACHED_IMAGE_FILE = "Resize image in disk cache [%s]";
    private static final String LOG_PREPROCESS_IMAGE = "PreProcess image before caching in memory [%s]";
    private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
    private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
    private static final String LOG_CACHE_IMAGE_ON_DISK = "Cache image on disk [%s]";
    private static final String LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISK = "Process image before cache on disk [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";
    private static final String LOG_TASK_INTERRUPTED = "Task was interrupted [%s]";

    private static final String ERROR_NO_IMAGE_STREAM = "No stream for image [%s]";
    private static final String ERROR_PRE_PROCESSOR_NULL = "Pre-processor returned null [%s]";
    private static final String ERROR_POST_PROCESSOR_NULL = "Post-processor returned null [%s]";
    private static final String ERROR_PROCESSOR_FOR_DISK_CACHE_NULL = "Bitmap processor for disk cache returned null [%s]";

    private final ImageLoaderEngine engine;
    //图片加载的信息
    private final ImageLoadingInfo imageLoadingInfo;
    private final Handler handler;

    // Helper references
    private final ImageLoaderConfiguration configuration;
    private final ImageDownloader downloader;
    private final ImageDownloader networkDeniedDownloader;
    private final ImageDownloader slowNetworkDownloader;
    private final ImageDecoder decoder;
    final String uri;
    private final String memoryCacheKey;
    final ImageAware imageAware;
    private final ImageSize targetSize;
    final DisplayImageOptions options;
    final ImageLoadingListener listener;
    final ImageLoadingProgressListener progressListener;
    // 是否同步加载，通过 Builder 构建的对象默认为 false。
    private final boolean syncLoading;

    // State vars
    private LoadedFrom loadedFrom = LoadedFrom.NETWORK;

    public LoadAndDisplayImageTask(ImageLoaderEngine engine, ImageLoadingInfo imageLoadingInfo, Handler handler) {
        this.engine = engine;
        this.imageLoadingInfo = imageLoadingInfo;
        this.handler = handler;

        configuration = engine.configuration;
        downloader = configuration.downloader;
        networkDeniedDownloader = configuration.networkDeniedDownloader;
        slowNetworkDownloader = configuration.slowNetworkDownloader;
        decoder = configuration.decoder;
        uri = imageLoadingInfo.uri;
        memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        imageAware = imageLoadingInfo.imageAware;
        targetSize = imageLoadingInfo.targetSize;
        options = imageLoadingInfo.options;
        listener = imageLoadingInfo.listener;
        progressListener = imageLoadingInfo.progressListener;
        syncLoading = options.isSyncLoading();
    }


    //获取图片并显示

    /**
     * 先是从内存缓存中去读取 bitmap 对象，若 bitmap 对象不存在，则调用 tryLoadBitmap() 函数获取 bitmap 对象，
     * 获取成功后若在 DisplayImageOptions.Builder 中设置了 cacheInMemory(true), 同时将 bitmap 对象缓存到内存中。
     * 最后新建DisplayBitmapTask显示图片。
     */
    @Override
    public void run() {
        //如果ImageLoaderEngine处于暂停状态，则等待
        if (waitIfPaused()) {
            return;
        }
        //如果需要在开始加载前的延时，则等待
        if (delayIfNeed()) {
            return;
        }
        //如果图片正在加载等待
        ReentrantLock loadFromUriLock = imageLoadingInfo.loadFromUriLock;
        L.d(LOG_START_DISPLAY_IMAGE_TASK, memoryCacheKey);
        if (loadFromUriLock.isLocked()) {
            L.d(LOG_WAITING_FOR_IMAGE_LOADED, memoryCacheKey);
        }
        loadFromUriLock.lock();
        Bitmap bitmap;
        try {
            //检查view是否被回收或者重用
            checkTaskNotActual();
            //从内存缓存中取出图片
            bitmap = configuration.memoryCache.get(memoryCacheKey);
            //图片是否被回收或者为空
            if (null == bitmap || bitmap.isRecycled()) {
                bitmap = tryLoadBitmap();
                // listener callback already was fired
                if (null == bitmap) {
                    return;
                }
                //view 是否被回收和复用
                checkTaskNotActual();
                //线程是不是被阻断
                checkTaskInterrupted();
                //是否需要预处理
                if (options.shouldPreProcess()) {
                    L.d(LOG_PREPROCESS_IMAGE, memoryCacheKey);
                    //调用process进行预处理
                    bitmap = options.getPreProcessor().process(bitmap);
                    if (null == bitmap) {
                        L.e(ERROR_PRE_PROCESSOR_NULL, memoryCacheKey);
                    }
                }
                //是否需要保存到内存中
                if (bitmap != null && options.isCacheInMemory()) {
                    L.d(LOG_CACHE_IMAGE_IN_MEMORY, memoryCacheKey);
                    configuration.memoryCache.put(memoryCacheKey, bitmap);
                }
            } else {
                loadedFrom = LoadedFrom.MEMORY_CACHE;
                L.d(LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING, memoryCacheKey);
            }
            //是否需要后续处理
            if (null != bitmap && options.shouldPostProcess()) {
                L.d(LOG_POSTPROCESS_IMAGE, memoryCacheKey);
                bitmap = options.getPostProcessor().process(bitmap);
                if (null == bitmap) {
                    L.d(ERROR_POST_PROCESSOR_NULL, memoryCacheKey);
                }
            }
        } catch (TaskCancelledException e) {
            fireCancelEvent();
            return;
        } finally {
            loadFromUriLock.unlock();
        }
        //新建DisplayTask展现图片
        DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(bitmap, imageLoadingInfo, engine, loadedFrom);
        runTask(displayBitmapTask, syncLoading, handler, engine);
    }

    /**
     * cancel的回调
     */
    private void fireCancelEvent() {
        if (syncLoading || isTaskInterrupted()) {
            return;
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                listener.onLoadingCancelled(uri, imageAware.getWrappedView());
            }
        };
        runTask(r, false, handler, engine);
    }

    /**
     * 从磁盘缓存或网络获取图片
     * 步骤：
     * 根据 uri 看看磁盘中是不是已经缓存了这个文件，如果已经缓存，调用 decodeImage 函数，将图片文件 decode 成 bitmap 对象；
     * 如果 bitmap 不合法或缓存文件不存在，判断是否需要缓存在磁盘，需要则调用tryCacheImageOnDisk()函数去下载并缓存图片到本地磁盘，
     * 再通过decodeImage(imageUri)函数将图片文件 decode 成 bitmap 对象，否则直接通过decodeImage(imageUriForDecoding)下载图片并解析。
     *
     * @return
     */
    private Bitmap tryLoadBitmap() throws TaskCancelledException {
        Bitmap bmp = null;
        try {
            File imageFile = configuration.diskCache.get(uri);
            // 根据 uri 看看磁盘中是不是已经缓存了这个文件，如果已经缓存
            if (null != imageFile && imageFile.exists() && imageFile.length() > 0) {
                L.d(LOG_LOAD_IMAGE_FROM_DISK_CACHE, memoryCacheKey);
                loadedFrom = LoadedFrom.DISC_CACHE;
                checkTaskNotActual();
                bmp = decodeImage(uri);
            }
            //bitmap 不合法或缓存文件不存在，判断是否需要缓存在磁盘，需要则调用tryCacheImageOnDisk()函数去下载并缓存图片到本地磁盘，
            if (null == bmp || bmp.getWidth() <= 0 || bmp.getHeight() <= 0) {
                L.d(LOG_LOAD_IMAGE_FROM_NETWORK, memoryCacheKey);
                loadedFrom = LoadedFrom.NETWORK;
                String imageUriForDecoding = uri;
                if (options.isCacheOnDisk() && tryCacheImageOnDisk()) {
                    imageFile = configuration.diskCache.get(uri);
                    if (imageFile != null) {
                        imageUriForDecoding = ImageDownloader.Scheme.FILE.wrap(imageFile.getAbsolutePath());
                    }
                }
                checkTaskNotActual();
                bmp = decodeImage(imageUriForDecoding);
                if (null == bmp || bmp.getWidth() <= 0 || bmp.getHeight() <= 0) {
                    fireFailEvent(FailReason.FailType.DECODING_ERROR, null);
                }
            }
        } catch (IllegalStateException e) {
            fireFailEvent(FailReason.FailType.NETWORK_DENIED, null);
        } catch (TaskCancelledException e) {
            throw e;
        } catch (IOException e) {
            L.e(e);
            fireFailEvent(FailReason.FailType.IO_ERROR, e);
        } catch (OutOfMemoryError e) {
            L.e(e);
            fireFailEvent(FailReason.FailType.OUT_OF_MEMORY, e);
        } catch (Throwable e) {
            L.e(e);
            fireFailEvent(FailReason.FailType.UNKNOWN, e);
        }
        return bmp;
    }

    /**
     * @param failType
     * @param failCause
     */
    private void fireFailEvent(final FailReason.FailType failType, final Throwable failCause) {
        if (syncLoading || isTaskInterrupted() || isTaskNotActual()) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (options.shouldShowImageOnFail()) {
                        imageAware.setImageDrawable(options.getImageOnFail(configuration.resources));
                    }
                    listener.onLoadingFailed(uri, imageAware.getWrappedView(), new FailReason(failType, failCause));
                }
            };
            runTask(r, false, handler, engine);
        }
    }

    /**
     * @param r
     * @param sync
     * @param handler
     * @param engine
     */
    static void runTask(Runnable r, boolean sync, Handler handler, ImageLoaderEngine engine) {
        if (sync) {
            r.run();
        } else if (null == handler) {
            engine.fireCallback(r);
        } else {
            handler.post(r);
        }
    }

    private void checkTaskInterrupted() throws TaskCancelledException {
        if (isTaskInterrupted()) {
            throw new TaskCancelledException();
        }
    }

    private boolean isTaskInterrupted() {
        if (Thread.interrupted()) {
            L.d(LOG_TASK_INTERRUPTED, memoryCacheKey);
            return true;
        }
        return false;
    }

    /**
     * 下载图片并存储在磁盘内，根据磁盘缓存图片最长宽高的配置处理图片。
     * 主要就是这一句话，调用下载器下载并保存图片。
     * 如果你在ImageLoaderConfiguration中还配置了maxImageWidthForDiskCache或者maxImageHeightForDiskCache，
     * 还会调用resizeAndSaveImage()函数，调整图片尺寸，并保存新的图片文件。
     *
     * @return
     */
    private boolean tryCacheImageOnDisk() throws TaskCancelledException {
        L.d(LOG_CACHE_IMAGE_ON_DISK, memoryCacheKey);
        boolean loaded;
        try {
            loaded = downloadImage();
            if (loaded) {
                int width = configuration.maxImageWidthForDiskCache;
                int height = configuration.maxImageHeightForDiskCache;
                if (width > 0 || height > 0) {
                    L.d(LOG_RESIZE_CACHED_IMAGE_FILE, memoryCacheKey);
                    resizeAndSaveImage(width, height); // TODO : process boolean result
                }
            }
        } catch (IOException e) {
            L.e(e);
            loaded = false;
        }
        return loaded;

    }

    /**
     * 下载图片并存储在磁盘内。调用getDownloader()得到ImageDownloader去下载图片。
     * @return
     */
    private boolean downloadImage() throws IOException {
        InputStream is = getDownloader().getStream(uri,options.getExtraForDownloader());
        if (null == is){
            L.e(ERROR_NO_IMAGE_STREAM, memoryCacheKey);
            return false;
        }else {
            try {
                return configuration.diskCache.save(uri,is,this);
            }finally {
                IoUtils.closeSilently(is);
            }
        }
    }

    /**
     * 从磁盘缓存中得到图片，重新设置大小及进行一些处理后保存。
     * @param width
     * @param height
     */
    private boolean resizeAndSaveImage(int width, int height) throws IOException {
        //Decode image file, compress and re-save it
        boolean saved = false;
        File targetFile = configuration.diskCache.get(uri);
        if (null != targetFile && targetFile.exists()){
            ImageSize targetImageSize = new ImageSize(width,height);
            DisplayImageOptions specialOptions = new DisplayImageOptions.Builder()
                    .cloneFrom(options)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .build();
            ImageDecodingInfo decodingInfo = new ImageDecodingInfo(memoryCacheKey, ImageDownloader.Scheme.FILE.wrap(targetFile.getAbsolutePath()),
                    uri,targetImageSize,ViewScaleType.FIT_INSIDE,getDownloader(),specialOptions);
            Bitmap bitmap = decoder.decode(decodingInfo);
            if (bitmap != null && configuration.processorForDiskCache != null) {
                L.d(LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISK, memoryCacheKey);
                bitmap = configuration.processorForDiskCache.process(bitmap);
                if (bitmap == null) {
                    L.e(ERROR_PROCESSOR_FOR_DISK_CACHE_NULL, memoryCacheKey);
                }
            }
            if (null != bitmap){
                saved = configuration.diskCache.save(uri,bitmap);
                bitmap.recycle();
            }
        }
        return saved;
    }



    /**
     * 生成bitmap
     *
     * @param imageUri
     * @return
     * @throws IOException
     */
    private Bitmap decodeImage(String imageUri) throws IOException {
        ViewScaleType scaleType = imageAware.getScaleType();
        ImageDecodingInfo imageDecodingInfo = new ImageDecodingInfo(memoryCacheKey, imageUri, uri, targetSize, scaleType, getDownloader(), options);
        return decoder.decode(imageDecodingInfo);
    }

    /**
     * 判断有没有开始加载前的延时
     *
     * @return
     */
    private boolean delayIfNeed() {
        if (options.shouldDelayBeforeLoading()) {
            L.d(LOG_DELAY_BEFORE_LOADING, options.getDelayBeforeLoading(), memoryCacheKey);
            try {
                Thread.sleep(options.getDelayBeforeLoading());
            } catch (InterruptedException e) {
                L.e(LOG_TASK_INTERRUPTED, memoryCacheKey);
                e.printStackTrace();
            }
            return isTaskNotActual();
        }
        return false;
    }

    /**
     * 如果ImageLoaderEngine是否暂停状态
     *
     * @return
     */
    private boolean waitIfPaused() {
        AtomicBoolean pause = engine.getPause();
        if (pause.get()) {
            synchronized (engine.getPauseLock()) {
                if (pause.get()) {
                    L.d(LOG_WAITING_FOR_RESUME, memoryCacheKey);
                    try {
                        engine.getPauseLock().wait();
                    } catch (InterruptedException e) {
                        L.e(LOG_TASK_INTERRUPTED, memoryCacheKey);
                        e.printStackTrace();
                        return true;
                    }
                    L.d(LOG_RESUME_AFTER_PAUSE, memoryCacheKey);
                }
            }
        }
        return isTaskNotActual();
    }

    @Override
    public boolean onBytesCopied(int current, int total) {
        return syncLoading || fireProgressEvent(current, total);
    }

    private boolean fireProgressEvent(final int current, final int total) {
        if (isTaskInterrupted() || isTaskNotActual()) return false;
        if (progressListener != null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    progressListener.onProgressUpdate(uri, imageAware.getWrappedView(), current, total);
                }
            };
            runTask(r, false, handler, engine);
        }
        return true;

    }

    /**
     * 检查view是否被回收或者重用
     *
     * @throws TaskCancelledException
     */
    private void checkTaskNotActual() throws TaskCancelledException {
        checkViewCollected();
        checkViewReused();
    }

    /**
     * 检查view是否被回收
     */
    private void checkViewCollected() throws TaskCancelledException {
        if (isViewCollected()) {
            throw new TaskCancelledException();
        }
    }

    /**
     * 检查view是否被重用
     *
     * @throws TaskCancelledException
     */
    private void checkViewReused() throws TaskCancelledException {
        if (isViewReused()) {
            throw new TaskCancelledException();
        }
    }


    private boolean isTaskNotActual() {
        return isViewCollected() || isViewReused();
    }

    private boolean isViewCollected() {
        if (imageAware.isCollected()) {
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED, memoryCacheKey);
            return true;
        }
        return false;
    }

    private boolean isViewReused() {
        String currentCacheKey = engine.getLoadingUriForView(imageAware);
        boolean imageAwareWasReused = !memoryCacheKey.equals(currentCacheKey);
        if (imageAwareWasReused) {
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_REUSED, memoryCacheKey);
            return true;
        }
        return false;
    }

    public ImageDownloader getDownloader() {
        ImageDownloader d;
        if (engine.isNetworkDenied()) {
            d = networkDeniedDownloader;
        } else if (engine.isSlowNetwork()) {
            d = slowNetworkDownloader;
        } else {
            d = downloader;
        }
        return d;
    }

    public String getLoadingUri(){
        return uri;
    }

    class TaskCancelledException extends Exception {
    }
}
