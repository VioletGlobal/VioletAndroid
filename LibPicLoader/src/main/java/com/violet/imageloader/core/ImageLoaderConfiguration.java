package com.violet.imageloader.core;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.violet.imageloader.cache.disc.DiskCache;
import com.violet.imageloader.cache.disc.naming.FileNameGenerator;
import com.violet.imageloader.cache.memory.MemoryCache;
import com.violet.imageloader.cache.memory.impl.FuzzyKeyMemoryCache;
import com.violet.imageloader.core.assist.FlushedInputStream;
import com.violet.imageloader.core.assist.ImageSize;
import com.violet.imageloader.core.assist.QueueProcessingType;
import com.violet.imageloader.core.decode.ImageDecoder;
import com.violet.imageloader.core.downloader.ImageDownloader;
import com.violet.imageloader.core.process.BitmapProcessor;
import com.violet.imageloader.utils.L;
import com.violet.imageloader.utils.MemoryCacheUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

/**
 * Created by kan212 on 2018/8/29.
 * ImageLoader的配置信息，包括图片最大尺寸、线程池、缓存、下载器、解码器等等。
 */

public class ImageLoaderConfiguration {

    //程序本地资源访问器，用于加载DisplayImageOptions中设置的一些 App 中图片资源。
    final Resources resources;
    //内存缓存的图片最大宽度。
    final int maxImageWidthForMemoryCache;
    //内存缓存的图片最大高度
    final int maxImageHeightForMemoryCache;
    //磁盘缓存的图片最大宽度。
    final int maxImageWidthForDiskCache;
    //磁盘缓存的图片最大高度。
    final int maxImageHeightForDiskCache;
    //图片处理器，用于处理从磁盘缓存中读取到的图片。
    final BitmapProcessor processorForDiskCache;
    //ImageLoaderEngine中用于执行从源获取图片任务的 Executor。
    final Executor taskExecutor;
    //ImageLoaderEngine中用于执行从缓存获取图片任务的 Executor。
    final Executor taskExecutorForCachedImages;
    //用户是否自定义了上面的 taskExecutor。
    final boolean customExecutor;
    //用户是否自定义了上面的 taskExecutorForCachedImages。
    final boolean customExecutorForCachedImages;
    //上面两个默认线程池的核心池大小，即最大并发数。
    final int threadPoolSize;
    //上面两个默认线程池的线程优先级。
    final int threadPriority;
    //上面两个默认线程池的线程队列类型。目前只有 FIFO, LIFO 两种可供选择。
    final QueueProcessingType tasksProcessingType;
    //图片内存缓存。
    final MemoryCache memoryCache;
    //图片磁盘缓存，一般放在 SD 卡。
    final DiskCache diskCache;
    //图片下载器。
    final ImageDownloader downloader;
    //图片解码器，内部可使用我们常用的BitmapFactory.decode(…)将图片资源解码成Bitmap对象。
    final ImageDecoder decoder;
    //图片显示的配置项。比如加载前、加载中、加载失败应该显示的占位图片，图片是否需要在磁盘缓存，是否需要在内存缓存等。
    final DisplayImageOptions defaultDisplayImageOptions;
    //不允许访问网络的图片下载器。
    final ImageDownloader networkDeniedDownloader;
    //慢网络情况下的图片下载器。
    final ImageDownloader slowNetworkDownloader;

    private ImageLoaderConfiguration(final Builder builder) {
        resources = builder.context.getResources();
        maxImageWidthForMemoryCache = builder.maxImageWidthForMemoryCache;
        maxImageHeightForMemoryCache = builder.maxImageHeightForMemoryCache;
        maxImageWidthForDiskCache = builder.maxImageWidthForDiskCache;
        maxImageHeightForDiskCache = builder.maxImageHeightForDiskCache;
        processorForDiskCache = builder.processorForDiskCache;
        taskExecutor = builder.taskExecutor;
        taskExecutorForCachedImages = builder.taskExecutorForCachedImages;
        threadPoolSize = builder.threadPoolSize;
        threadPriority = builder.threadPriority;
        tasksProcessingType = builder.tasksProcessingType;
        diskCache = builder.diskCache;
        memoryCache = builder.memoryCache;
        defaultDisplayImageOptions = builder.defaultDisplayImageOptions;
        downloader = builder.downloader;
        decoder = builder.decoder;

        customExecutor = builder.customExecutor;
        customExecutorForCachedImages = builder.customExecutorForCachedImages;

        networkDeniedDownloader = new NetworkDeniedImageDownloader(downloader);
        slowNetworkDownloader = new SlowNetworkImageDownloader(downloader);

        L.writeDebugLogs(builder.writeLogs);
    }

    ImageSize getMaxImageSize() {
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        int width = maxImageWidthForMemoryCache;
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }
        int height = maxImageHeightForMemoryCache;
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        return new ImageSize(width, height);
    }


    public static ImageLoaderConfiguration createDefault(Context context) {
        return new Builder(context).build();
    }

    public static class Builder {
        private static final String WARNING_OVERLAP_DISK_CACHE_PARAMS = "diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other";
        private static final String WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR = "diskCache() and diskCacheFileNameGenerator() calls overlap each other";
        private static final String WARNING_OVERLAP_MEMORY_CACHE = "memoryCache() and memoryCacheSize() calls overlap each other";
        private static final String WARNING_OVERLAP_EXECUTOR = "threadPoolSize(), threadPriority() and tasksProcessingOrder() calls "
                + "can overlap taskExecutor() and taskExecutorForCachedImages() calls.";

        public static final int DEFAULT_THREAD_POOL_SIZE = 3;

        public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;

        public static final QueueProcessingType DEFAULT_TASK_PROCESSING_TYPE = QueueProcessingType.FIFO;

        private Context context;

        private int maxImageWidthForMemoryCache = 0;
        private int maxImageHeightForMemoryCache = 0;
        private int maxImageWidthForDiskCache = 0;
        private int maxImageHeightForDiskCache = 0;
        private BitmapProcessor processorForDiskCache = null;

        private Executor taskExecutor = null;
        private Executor taskExecutorForCachedImages = null;
        private boolean customExecutor = false;
        private boolean customExecutorForCachedImages = false;

        private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
        private int threadPriority = DEFAULT_THREAD_PRIORITY;
        private boolean denyCacheImageMultipleSizesInMemory = false;
        private QueueProcessingType tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;

        private int memoryCacheSize = 0;
        private long diskCacheSize = 0;
        private int diskCacheFileCount = 0;

        private MemoryCache memoryCache = null;
        private DiskCache diskCache = null;
        private FileNameGenerator diskCacheFileNameGenerator = null;
        private ImageDownloader downloader = null;
        private ImageDecoder decoder;
        private DisplayImageOptions defaultDisplayImageOptions = null;

        private boolean writeLogs = false;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        /**
         * memorycache的最大宽高
         *
         * @param maxImageWidthForMemoryCache
         * @param maxImageHeightForMemoryCache
         * @return
         */
        public Builder memoryCacheExtraOptions(int maxImageWidthForMemoryCache, int maxImageHeightForMemoryCache) {
            this.maxImageWidthForMemoryCache = maxImageWidthForMemoryCache;
            this.maxImageHeightForMemoryCache = maxImageHeightForMemoryCache;
            return this;
        }

        /**
         * Sets options for resizing/compressing of downloaded images before saving to disk cache.<br />
         *
         * @param maxImageWidthForDiskCache
         * @param maxImageHeightForDiskCache
         * @param processorForDiskCache
         * @return
         */
        public Builder diskCacheExtraOptions(int maxImageWidthForDiskCache, int maxImageHeightForDiskCache,
                                             BitmapProcessor processorForDiskCache) {
            this.maxImageWidthForDiskCache = maxImageWidthForDiskCache;
            this.maxImageHeightForDiskCache = maxImageHeightForDiskCache;
            this.processorForDiskCache = processorForDiskCache;
            return this;
        }

        /**
         * Sets custom {@linkplain Executor executor} for tasks of loading and displaying images.<br />
         *
         * @param executor
         * @return
         */
        public Builder taskExecutor(Executor executor) {
            if (threadPoolSize != DEFAULT_THREAD_POOL_SIZE || threadPriority != DEFAULT_THREAD_PRIORITY
                    || tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }
            this.taskExecutor = executor;
            return this;
        }

        /**
         * Sets custom {@linkplain Executor executor} for tasks of displaying <b>cached on disk</b> images (these tasks
         * are executed quickly so UIL prefer to use separate executor for them).<br />
         *
         * @param executorForCachedImages
         * @return
         */
        public Builder taskExecutorForCachedImages(Executor executorForCachedImages) {
            if (threadPoolSize != DEFAULT_THREAD_POOL_SIZE || threadPriority != DEFAULT_THREAD_PRIORITY || tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }

            this.taskExecutorForCachedImages = executorForCachedImages;
            return this;
        }

        public Builder threadPoolSize(int threadPoolSize) {
            if (taskExecutor != null || taskExecutorForCachedImages != null) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }

            this.threadPoolSize = threadPoolSize;
            return this;
        }

        /**
         * Sets the priority for image loading threads. Should be <b>NOT</b> greater than {@link Thread#MAX_PRIORITY} or
         * less than {@link Thread#MIN_PRIORITY}<br />
         * Default value - {@link #DEFAULT_THREAD_PRIORITY this}
         */
        public Builder threadPriority(int threadPriority) {
            if (taskExecutor != null || taskExecutorForCachedImages != null) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }

            if (threadPriority < Thread.MIN_PRIORITY) {
                this.threadPriority = Thread.MIN_PRIORITY;
            } else {
                if (threadPriority > Thread.MAX_PRIORITY) {
                    this.threadPriority = Thread.MAX_PRIORITY;
                } else {
                    this.threadPriority = threadPriority;
                }
            }
            return this;
        }

        /**
         * When you display an image in a small {@link android.widget.ImageView ImageView} and later you try to display
         * this image (from identical URI) in a larger {@link android.widget.ImageView ImageView} so decoded image of
         * bigger size will be cached in memory as a previous decoded image of smaller size.<br />
         * So <b>the default behavior is to allow to cache multiple sizes of one image in memory</b>. You can
         * <b>deny</b> it by calling <b>this</b> method: so when some image will be cached in memory then previous
         * cached size of this image (if it exists) will be removed from memory cache before.
         */
        public Builder denyCacheImageMultipleSizesInMemory() {
            this.denyCacheImageMultipleSizesInMemory = true;
            return this;
        }

        /**
         * Sets type of queue processing for tasks for loading and displaying images.<br />
         * Default value - {@link QueueProcessingType#FIFO}
         */
        public Builder tasksProcessingOrder(QueueProcessingType tasksProcessingType) {
            if (taskExecutor != null || taskExecutorForCachedImages != null) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }

            this.tasksProcessingType = tasksProcessingType;
            return this;
        }

        /**
         * Sets maximum memory cache size for {@link android.graphics.Bitmap bitmaps} (in bytes).<br />
         * Default value - 1/8 of available app memory.<br />
         * {@link MemoryCache}.
         */
        public Builder memoryCacheSize(int memoryCacheSize) {
            if (memoryCacheSize <= 0)
                throw new IllegalArgumentException("memoryCacheSize must be a positive number");

            if (memoryCache != null) {
                L.w(WARNING_OVERLAP_MEMORY_CACHE);
            }

            this.memoryCacheSize = memoryCacheSize;
            return this;
        }

        public Builder memoryCacheSizePercentage(int availableMemoryPercent) {
            if (availableMemoryPercent <= 0 || availableMemoryPercent >= 100) {
                throw new IllegalArgumentException("availableMemoryPercent must be in range (0 < % < 100)");
            }

            if (memoryCache != null) {
                L.w(WARNING_OVERLAP_MEMORY_CACHE);
            }

            long availableMemory = Runtime.getRuntime().maxMemory();
            memoryCacheSize = (int) (availableMemory * (availableMemoryPercent / 100f));
            return this;
        }

        /**
         * Sets memory cache for {@link android.graphics.Bitmap bitmaps}.<br />
         * Default value - {LruMemoryCache}
         * with limited memory cache size (size = 1/8 of available app memory)<br />
         * <br />
         * <b>NOTE:</b> If you set custom memory cache then following configuration option will not be considered:
         * <ul>
         * <li>{@link #memoryCacheSize(int)}</li>
         * </ul>
         */
        public Builder memoryCache(MemoryCache memoryCache) {
            if (memoryCacheSize != 0) {
                L.w(WARNING_OVERLAP_MEMORY_CACHE);
            }

            this.memoryCache = memoryCache;
            return this;
        }

        /**
         * Sets maximum disk cache size for images (in bytes).<br />
         * By default: disk cache is unlimited.<br />
         * <b>NOTE:</b> If you use this method then
         * { LruDiskCache}
         * will be used as disk cache. You can use {@link #diskCache(DiskCache)} method for introduction your own
         * implementation of {@link DiskCache}
         */
        public Builder diskCacheSize(int maxCacheSize) {
            if (maxCacheSize <= 0)
                throw new IllegalArgumentException("maxCacheSize must be a positive number");

            if (diskCache != null) {
                L.w(WARNING_OVERLAP_DISK_CACHE_PARAMS);
            }

            this.diskCacheSize = maxCacheSize;
            return this;
        }

        /**
         * Sets maximum file count in disk cache directory.<br />
         * By default: disk cache is unlimited.<br />
         * <b>NOTE:</b> If you use this method then
         * {@linkLruDiskCache}
         * will be used as disk cache. You can use {@link #diskCache(DiskCache)} method for introduction your own
         * implementation of {@link DiskCache}
         */
        public Builder diskCacheFileCount(int maxFileCount) {
            if (maxFileCount <= 0)
                throw new IllegalArgumentException("maxFileCount must be a positive number");

            if (diskCache != null) {
                L.w(WARNING_OVERLAP_DISK_CACHE_PARAMS);
            }

            this.diskCacheFileCount = maxFileCount;
            return this;
        }

        /**
         * Sets name generator for files cached in disk cache.<br />
         * Default value -
         * DefaultConfigurationFactory.createFileNameGenerator()}
         */
        public Builder diskCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
            if (diskCache != null) {
                L.w(WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR);
            }

            this.diskCacheFileNameGenerator = fileNameGenerator;
            return this;
        }

        /**
         * set diskCache
         * If you set custom disk cache then following configuration option will not be considered:
         *
         * @param diskCache
         * @return
         */
        public Builder diskCache(DiskCache diskCache) {
            if (diskCacheSize > 0 || diskCacheFileCount > 0) {
                L.w(WARNING_OVERLAP_DISK_CACHE_PARAMS);
            }
            if (diskCacheFileNameGenerator != null) {
                L.w(WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR);
            }

            this.diskCache = diskCache;
            return this;
        }

        /**
         * Sets utility which will be responsible for downloading of image
         *
         * @param imageDownloader
         * @return
         */
        public Builder imageDownloader(ImageDownloader imageDownloader) {
            this.downloader = imageDownloader;
            return this;
        }

        /**
         * Sets utility which will be responsible for decoding of image stream.
         *
         * @param imageDecoder
         * @return
         */
        public Builder imageDecoder(ImageDecoder imageDecoder) {
            this.decoder = imageDecoder;
            return this;
        }

        /**
         * sets default {@linkplain DisplayImageOptions display image options} for image displaying
         *
         * @param defaultDisplayImageOptions
         * @return
         */
        public Builder defaultDisplayImageOptions(DisplayImageOptions defaultDisplayImageOptions) {
            this.defaultDisplayImageOptions = defaultDisplayImageOptions;
            return this;
        }

        /**
         * Enables detail logging of {@link ImageLoader} work. To prevent detail logs don't call this method.
         * Consider {@link L#disableLogging()} to disable
         * ImageLoader logging completely (even error logs)
         */
        public Builder writeDebugLogs() {
            this.writeLogs = true;
            return this;
        }

        /**
         * Builds configured {@link ImageLoaderConfiguration} object
         */
        public ImageLoaderConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new ImageLoaderConfiguration(this);
        }

        private void initEmptyFieldsWithDefaultValues() {
            //ImageLoaderEngine中用于执行从源获取图片任务的
            if (null == taskExecutor) {
                taskExecutor = DefaultConfigurationFactory
                        .createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
            } else {
                customExecutor = true;
            }

            if (null == taskExecutorForCachedImages) {
                taskExecutorForCachedImages = DefaultConfigurationFactory
                        .createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
            } else {
                customExecutorForCachedImages = true;
            }

            //diskCacheFileNameGenerator默认值为HashCodeFileNameGenerator。
            if (null == diskCache) {
                if (null == diskCacheFileNameGenerator) {
                    diskCacheFileNameGenerator = DefaultConfigurationFactory.createFileNameGenerator();
                }
                diskCache = DefaultConfigurationFactory
                        .createDiskCache(context, diskCacheFileNameGenerator, diskCacheSize, diskCacheFileCount);
            }
            if (memoryCache == null) {
                memoryCache = DefaultConfigurationFactory.createMemoryCache(context, memoryCacheSize);
            }
            if (denyCacheImageMultipleSizesInMemory) {
                memoryCache = new FuzzyKeyMemoryCache(memoryCache, MemoryCacheUtils.createFuzzyKeyComparator());
            }
            if (downloader == null) {
                downloader = DefaultConfigurationFactory.createImageDownloader(context);
            }
            if (decoder == null) {
                decoder = DefaultConfigurationFactory.createImageDecoder(writeLogs);
            }
            if (defaultDisplayImageOptions == null) {
                defaultDisplayImageOptions = DisplayImageOptions.createSimple();
            }

        }

    }

    private static class NetworkDeniedImageDownloader implements ImageDownloader {
        private final ImageDownloader wrappedDownloader;

        public NetworkDeniedImageDownloader(ImageDownloader wrappedDownloader) {
            this.wrappedDownloader = wrappedDownloader;
        }

        @Override
        public InputStream getStream(String imageUri, Object extra) throws IOException {
            switch (Scheme.ofUri(imageUri)) {
                case HTTP:
                case HTTPS:
                    throw new IllegalStateException();
                default:
                    return wrappedDownloader.getStream(imageUri, extra);
            }
        }
    }

    private static class SlowNetworkImageDownloader implements ImageDownloader{
        private final ImageDownloader wrappedDownloader;

        public SlowNetworkImageDownloader(ImageDownloader wrappedDownloader) {
            this.wrappedDownloader = wrappedDownloader;
        }

        @Override
        public InputStream getStream(String imageUri, Object extra) throws IOException {
            InputStream imageStream = wrappedDownloader.getStream(imageUri, extra);
            switch (Scheme.ofUri(imageUri)) {
                case HTTP:
                case HTTPS:
                    return new FlushedInputStream(imageStream);
                default:
                    return imageStream;
            }
        }
    }
}
