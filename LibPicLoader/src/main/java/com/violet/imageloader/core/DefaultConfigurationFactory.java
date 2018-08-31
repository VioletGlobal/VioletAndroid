package com.violet.imageloader.core;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.annotation.NonNull;

import com.violet.imageloader.cache.disc.DiskCache;
import com.violet.imageloader.cache.disc.impl.UnlimitedDiskCache;
import com.violet.imageloader.cache.disc.impl.ext.LruDiskCache;
import com.violet.imageloader.cache.disc.naming.FileNameGenerator;
import com.violet.imageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.violet.imageloader.cache.memory.MemoryCache;
import com.violet.imageloader.cache.memory.impl.LruMemoryCache;
import com.violet.imageloader.core.assist.QueueProcessingType;
import com.violet.imageloader.core.assist.deque.LIFOLinkedBlockingDeque;
import com.violet.imageloader.core.decode.BaseImageDecoder;
import com.violet.imageloader.core.decode.ImageDecoder;
import com.violet.imageloader.core.display.BitmapDisplayer;
import com.violet.imageloader.core.display.SimpleBitmapDisplayer;
import com.violet.imageloader.core.downloader.BaseImageDownloader;
import com.violet.imageloader.core.downloader.ImageDownloader;
import com.violet.imageloader.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kan212 on 2018/8/29.
 * 提供给ImageLoaderConfiguration的基本设置工厂
 */

public class DefaultConfigurationFactory {

    //生成一个纯粹展现的displayer
    public static BitmapDisplayer createBitmapDisplayer() {
        return new SimpleBitmapDisplayer();
    }

    /**
     * 创建线程池
     * 内部实现会调用createThreadFactory(…)返回一个支持线程优先级设置，
     * 并且以固定规则命名新建的线程的线程工厂类DefaultConfigurationFactory.DefaultThreadFactory。
     *
     * @param threadPoolSize      最大线程数
     * @param threadPriority      线程优先级
     * @param tasksProcessingType 队列类型
     * @return
     */
    public static Executor createExecutor(int threadPoolSize, int threadPriority, QueueProcessingType tasksProcessingType) {
        boolean lifo = tasksProcessingType == QueueProcessingType.LIFO;
        BlockingQueue<Runnable> taskQueue = lifo ? new LIFOLinkedBlockingDeque<Runnable>() :
                new LinkedBlockingQueue<Runnable>();
        return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue,
                createThreadFactory(threadPriority, "uil-pool-"));
    }

    private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    //返回一个HashCodeFileNameGenerator对象，即以 uri HashCode 为文件名的文件名生成器。
    public static FileNameGenerator createFileNameGenerator() {
        return new HashCodeFileNameGenerator();
    }

    /**
     * 创建一个 Disk Cache。如果 diskCacheSize 或者 diskCacheFileCount 大于 0，
     * 返回一个LruDiskCache，否则返回无大小限制的UnlimitedDiskCache。
     *
     * @param context
     * @param diskCacheFileNameGenerator
     * @param diskCacheSize
     * @param diskCacheFileCount
     * @return
     */
    public static DiskCache createDiskCache(Context context, FileNameGenerator diskCacheFileNameGenerator, long diskCacheSize, int diskCacheFileCount) {
        File reserveCacheDir = createReserveDiskCacheDir(context);
        if (diskCacheSize > 0 || diskCacheFileCount > 0) {
            File individualCacheDir = StorageUtils.getIndividualCacheDirectory(context);
            try {
                return new LruDiskCache(individualCacheDir, reserveCacheDir, diskCacheFileNameGenerator, diskCacheSize,
                        diskCacheFileCount);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File cacheFile = StorageUtils.getCacheDirectory(context);
        return new UnlimitedDiskCache(cacheFile, reserveCacheDir, diskCacheFileNameGenerator);
    }

    /**
     * Creates reserve disk cache folder which will be used if primary disk cache
     * folder becomes unavailable
     *
     * @param context
     * @return
     */
    private static File createReserveDiskCacheDir(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context, false);
        File individualDir = new File(cacheDir, "uil-images");
        if (individualDir.exists() || individualDir.mkdir()) {
            cacheDir = individualDir;
        }
        return cacheDir;
    }

    public static MemoryCache createMemoryCache(Context context, int memoryCacheSize) {
        if (0 == memoryCacheSize) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int memoryClass = am.getMemoryClass();
            if (hasHoneycomb() && isLargeHeap(context)){
                memoryClass = getLargeMemoryClass(am);
            }
            memoryCacheSize = 1024 * 1024 * memoryClass / 8;
        }
        return new LruMemoryCache(memoryCacheSize);
    }

    private static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static boolean isLargeHeap(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static int getLargeMemoryClass(ActivityManager am) {
        return am.getLargeMemoryClass();
    }

    public static ImageDownloader createImageDownloader(Context context) {
        return new BaseImageDownloader(context);
    }

    public static ImageDecoder createImageDecoder(boolean loggingEnabled) {
        return new BaseImageDecoder(loggingEnabled);
    }

    /**
     * 为ImageLoaderEngine中的任务分发器taskDistributor提供线程池，
     * 该线程池为 normal 优先级的无并发大小限制的线程池。
     * @return
     */
    public static Executor createTaskDistributor() {
        return Executors.newCachedThreadPool(createThreadFactory(Thread.NORM_PRIORITY,"uil-pool-d-"));
    }


    public static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            t.setPriority(threadPriority);
            return t;
        }

    }
}
