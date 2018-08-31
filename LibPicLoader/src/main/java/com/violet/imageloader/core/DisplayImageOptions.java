package com.violet.imageloader.core;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.violet.imageloader.core.assist.ImageScaleType;
import com.violet.imageloader.core.display.BitmapDisplayer;
import com.violet.imageloader.core.process.BitmapProcessor;

/**
 * Created by kan212 on 2018/8/29.
 * 图片显示的配置项。比如加载前、加载中、加载失败应该显示的占位图片，图片是否需要在磁盘缓存，是否需要在 memory 缓存等
 */

public final class DisplayImageOptions {
    //图片正在加载中的占位图片的 resource id，优先级比下面的imageOnLoading高，当存在时，imageOnLoading不起作用
    private final int imageResOnLoading;
    //空 uri 时的占位图片的 resource id，优先级比下面的imageForEmptyUri高，当存在时，imageForEmptyUri不起作用。
    private final int imageResForEmptyUri;
    //加载失败时的占位图片的 resource id，优先级比下面的imageOnFail高，当存在时，imageOnFail不起作用。
    private final int imageResOnFail;
    //加载中的占位图片的 drawabled 对象，默认为 null。
    private final Drawable imageOnLoading;
    //空 uri 时的占位图片的 drawabled 对象，默认为 null。
    private final Drawable imageForEmptyUri;
    //加载失败时的占位图片的 drawabled 对象，默认为 null。
    private final Drawable imageOnFail;
    //在加载前是否重置 view，通过 Builder 构建的对象默认为 false。
    private final boolean resetViewBeforeLoading;
    //是否缓存在内存中，通过 Builder 构建的对象默认为 false。
    private final boolean cacheInMemory;
    //是否缓存在磁盘中，通过 Builder 构建的对象默认为 false。
    private final boolean cacheOnDisk;
    //图片的缩放类型，通过 Builder 构建的对象默认为IN_SAMPLE_POWER_OF_2。
    private final ImageScaleType imageScaleType;
    //为 BitmapFactory.Options，用于BitmapFactory.decodeStream(imageStream, null, decodingOptions)得到图片尺寸等信息。
    private final BitmapFactory.Options decodingOptions;
    //设置在开始加载前的延迟时间，单位为毫秒，通过 Builder 构建的对象默认为 0。
    private final int delayBeforeLoading;
    //是否考虑图片的 EXIF 信息，通过 Builder 构建的对象默认为 false。
    private final boolean considerExifParams;
    //下载器需要的辅助信息。下载时传入ImageDownloader.getStream(String, Object)的对象，方便用户自己扩展，默认为 null。
    private final Object extraForDownloader;
    //缓存在内存之前的处理程序，默认为 null。
    private final BitmapProcessor preProcessor;
    //缓存在内存之后的处理程序，默认为 null。
    private final BitmapProcessor postProcessor;
    //图片的显示方式，通过 Builder 构建的对象默认为SimpleBitmapDisplayer。
    private final BitmapDisplayer displayer;
    //handler 对象，默认为 null。
    private final Handler handler;
    //是否同步加载，通过 Builder 构建的对象默认为 false。
    private final boolean isSyncLoading;

    private DisplayImageOptions(Builder builder) {
        imageResOnLoading = builder.imageResOnLoading;
        imageResForEmptyUri = builder.imageResForEmptyUri;
        imageResOnFail = builder.imageResOnFail;
        imageOnLoading = builder.imageOnLoading;
        imageForEmptyUri = builder.imageForEmptyUri;
        imageOnFail = builder.imageOnFail;
        resetViewBeforeLoading = builder.resetViewBeforeLoading;
        cacheInMemory = builder.cacheInMemory;
        cacheOnDisk = builder.cacheOnDisk;
        imageScaleType = builder.imageScaleType;
        decodingOptions = builder.decodingOptions;
        delayBeforeLoading = builder.delayBeforeLoading;
        considerExifParams = builder.considerExifParams;
        extraForDownloader = builder.extraForDownloader;
        preProcessor = builder.preProcessor;
        postProcessor = builder.postProcessor;
        displayer = builder.displayer;
        handler = builder.handler;
        isSyncLoading = builder.isSyncLoading;
    }

    public boolean shouldShowImageOnLoading() {
        return imageOnLoading != null || imageResOnLoading != 0;
    }

    public boolean shouldShowImageForEmptyUri() {
        return imageForEmptyUri != null || imageResForEmptyUri != 0;
    }

    public boolean shouldShowImageOnFail() {
        return imageOnFail != null || imageResOnFail != 0;
    }

    public boolean shouldPreProcess() {
        return preProcessor != null;
    }

    public boolean shouldPostProcess() {
        return postProcessor != null;
    }

    public boolean shouldDelayBeforeLoading() {
        return delayBeforeLoading > 0;
    }

    public Drawable getImageOnLoading(Resources res) {
        return imageResOnLoading != 0 ? res.getDrawable(imageResOnLoading) : imageOnLoading;
    }

    public Drawable getImageForEmptyUri(Resources res) {
        return imageResForEmptyUri != 0 ? res.getDrawable(imageResForEmptyUri) : imageForEmptyUri;
    }

    public Drawable getImageOnFail(Resources res) {
        return imageResOnFail != 0 ? res.getDrawable(imageResOnFail) : imageOnFail;
    }

    public boolean isResetViewBeforeLoading() {
        return resetViewBeforeLoading;
    }

    /**
     * 是否保存在内存中
     * @return
     */
    public boolean isCacheInMemory() {
        return cacheInMemory;
    }

    public boolean isCacheOnDisk() {
        return cacheOnDisk;
    }

    public ImageScaleType getImageScaleType() {
        return imageScaleType;
    }

    public BitmapFactory.Options getDecodingOptions() {
        return decodingOptions;
    }

    public int getDelayBeforeLoading() {
        return delayBeforeLoading;
    }

    public boolean isConsiderExifParams() {
        return considerExifParams;
    }

    public Object getExtraForDownloader() {
        return extraForDownloader;
    }

    public BitmapProcessor getPreProcessor() {
        return preProcessor;
    }

    public BitmapProcessor getPostProcessor() {
        return postProcessor;
    }

    public BitmapDisplayer getDisplayer() {
        return displayer;
    }

    public Handler getHandler() {
        return handler;
    }

    boolean isSyncLoading() {
        return isSyncLoading;
    }

    public static class Builder {
        private int imageResOnLoading = 0;
        private int imageResForEmptyUri = 0;
        private int imageResOnFail = 0;
        private Drawable imageOnLoading = null;
        private Drawable imageForEmptyUri = null;
        private Drawable imageOnFail = null;
        private boolean resetViewBeforeLoading = false;
        private boolean cacheInMemory = false;
        private boolean cacheOnDisk = false;
        private ImageScaleType imageScaleType = ImageScaleType.IN_SAMPLE_POWER_OF_2;
        private BitmapFactory.Options decodingOptions = new BitmapFactory.Options();
        private int delayBeforeLoading = 0;
        private boolean considerExifParams = false;
        private Object extraForDownloader = null;
        private BitmapProcessor preProcessor = null;
        private BitmapProcessor postProcessor = null;
        private BitmapDisplayer displayer = DefaultConfigurationFactory.createBitmapDisplayer();
        private Handler handler = null;
        private boolean isSyncLoading = false;

        public Builder showImageOnLoading(int imageRes) {
            imageResOnLoading = imageRes;
            return this;
        }

        public Builder showImageOnLoading(Drawable drawable) {
            imageOnLoading = drawable;
            return this;
        }

        public Builder showImageForEmptyUri(int imageRes) {
            imageResForEmptyUri = imageRes;
            return this;
        }

        public Builder showImageForEmptyUri(Drawable drawable) {
            imageForEmptyUri = drawable;
            return this;
        }

        public Builder showImageOnFail(int imageRes) {
            imageResOnFail = imageRes;
            return this;
        }

        public Builder showImageOnFail(Drawable drawable) {
            imageOnFail = drawable;
            return this;
        }

        public Builder resetViewBeforeLoading(boolean resetViewBeforeLoading) {
            this.resetViewBeforeLoading = resetViewBeforeLoading;
            return this;
        }

        public Builder cacheInMemory(boolean cacheInMemory) {
            this.cacheInMemory = cacheInMemory;
            return this;
        }

        public Builder cacheOnDisk(boolean cacheOnDisk) {
            this.cacheOnDisk = cacheOnDisk;
            return this;
        }

        public Builder imageScaleType(ImageScaleType imageScaleType) {
            this.imageScaleType = imageScaleType;
            return this;
        }

        /**
         * Sets {@link Bitmap.Config bitmap config} for image decoding. Default value - {@link Bitmap.Config#ARGB_8888}
         */
        public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
            if (bitmapConfig == null)
                throw new IllegalArgumentException("bitmapConfig can't be null");
            decodingOptions.inPreferredConfig = bitmapConfig;
            return this;
        }


        public Builder decodingOptions(BitmapFactory.Options decodingOptions) {
            if (decodingOptions == null)
                throw new IllegalArgumentException("decodingOptions can't be null");
            this.decodingOptions = decodingOptions;
            return this;
        }

        /**
         * Sets delay time before starting loading task. Default - no delay.
         */
        public Builder delayBeforeLoading(int delayInMillis) {
            this.delayBeforeLoading = delayInMillis;
            return this;
        }

        /**
         * Sets auxiliary object which will be passed to {@link ImageDownloader#getStream(String, Object)}
         */
        public Builder extraForDownloader(Object extra) {
            this.extraForDownloader = extra;
            return this;
        }

        /**
         * Sets whether ImageLoader will consider EXIF parameters of JPEG image (rotate, flip)
         */
        public Builder considerExifParams(boolean considerExifParams) {
            this.considerExifParams = considerExifParams;
            return this;
        }

        /**
         * Sets bitmap processor which will be process bitmaps before they will be cached in memory. So memory cache
         * will contain bitmap processed by incoming preProcessor.<br />
         * Image will be pre-processed even if caching in memory is disabled.
         */
        public Builder preProcessor(BitmapProcessor preProcessor) {
            this.preProcessor = preProcessor;
            return this;
        }

        public Builder postProcessor(BitmapProcessor postProcessor) {
            this.postProcessor = postProcessor;
            return this;
        }

        /**
         * Sets custom {@link BitmapDisplayer displayer} for image loading task. Default value -
         * {@link DefaultConfigurationFactory#createBitmapDisplayer()}
         */
        public Builder displayer(BitmapDisplayer displayer) {
            if (displayer == null) throw new IllegalArgumentException("displayer can't be null");
            this.displayer = displayer;
            return this;
        }

        Builder syncLoading(boolean isSyncLoading) {
            this.isSyncLoading = isSyncLoading;
            return this;
        }

        /**
         * Sets custom {@linkplain Handler handler} for displaying images and firing {@linkplain ImageLoadingListener
         * listener} events.
         */
        public Builder handler(Handler handler) {
            this.handler = handler;
            return this;
        }

        /**
         * Sets all options equal to incoming options
         */
        public Builder cloneFrom(DisplayImageOptions options) {
            imageResOnLoading = options.imageResOnLoading;
            imageResForEmptyUri = options.imageResForEmptyUri;
            imageResOnFail = options.imageResOnFail;
            imageOnLoading = options.imageOnLoading;
            imageForEmptyUri = options.imageForEmptyUri;
            imageOnFail = options.imageOnFail;
            resetViewBeforeLoading = options.resetViewBeforeLoading;
            cacheInMemory = options.cacheInMemory;
            cacheOnDisk = options.cacheOnDisk;
            imageScaleType = options.imageScaleType;
            decodingOptions = options.decodingOptions;
            delayBeforeLoading = options.delayBeforeLoading;
            considerExifParams = options.considerExifParams;
            extraForDownloader = options.extraForDownloader;
            preProcessor = options.preProcessor;
            postProcessor = options.postProcessor;
            displayer = options.displayer;
            handler = options.handler;
            isSyncLoading = options.isSyncLoading;
            return this;
        }

        /**
         * Builds configured {@link DisplayImageOptions} object
         */
        public DisplayImageOptions build() {
            return new DisplayImageOptions(this);
        }
    }

    public static DisplayImageOptions createSimple() {
        return new Builder().build();
    }

}
