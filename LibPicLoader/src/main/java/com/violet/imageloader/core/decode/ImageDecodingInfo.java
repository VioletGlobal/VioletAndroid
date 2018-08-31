package com.violet.imageloader.core.decode;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build;

import com.violet.imageloader.core.DisplayImageOptions;
import com.violet.imageloader.core.assist.ImageScaleType;
import com.violet.imageloader.core.assist.ImageSize;
import com.violet.imageloader.core.assist.ViewScaleType;
import com.violet.imageloader.core.downloader.ImageDownloader;


/**
 * Created by kan212 on 2018/8/29.
 * Image Decode 需要的信息。
 */

public class ImageDecodingInfo {

    //图片
    private final String imageKey;
    //图片 uri，可能是缓存文件的 uri
    private final String imageUri;
    //图片原 uri。
    private final String originalImageUri;
    //图片的显示尺寸。
    private final ImageSize targetSize;
    //图片的 ScaleType。
    private final ImageScaleType imageScaleType;
    // 图片的缩放选择
    private final ViewScaleType viewScaleType;
    // 图片的下载器。
    private final ImageDownloader downloader;
    //下载器需要的辅助信息。
    private final Object extraForDownloader;
    //是否需要考虑图片 Exif 信息
    private final boolean considerExifParams;
    //图片的解码信息，为 BitmapFactory.Options。
    private final BitmapFactory.Options decodingOptions;

    public ImageDecodingInfo(String imageKey, String imageUri, String originalImageUri, ImageSize targetSize, ViewScaleType viewScaleType,
                             ImageDownloader downloader, DisplayImageOptions displayOptions) {
        this.imageKey = imageKey;
        this.imageUri = imageUri;
        this.originalImageUri = originalImageUri;
        this.targetSize = targetSize;

        this.imageScaleType = displayOptions.getImageScaleType();
        this.viewScaleType = viewScaleType;

        this.downloader = downloader;
        this.extraForDownloader = displayOptions.getExtraForDownloader();

        considerExifParams = displayOptions.isConsiderExifParams();
        decodingOptions = new BitmapFactory.Options();
        copyOptions(displayOptions.getDecodingOptions(), decodingOptions);
    }

    private void copyOptions(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
        destOptions.inDensity = srcOptions.inDensity;
        destOptions.inDither = srcOptions.inDither;
        destOptions.inInputShareable = srcOptions.inInputShareable;
        destOptions.inJustDecodeBounds = srcOptions.inJustDecodeBounds;
        destOptions.inPreferredConfig = srcOptions.inPreferredConfig;
        destOptions.inPurgeable = srcOptions.inPurgeable;
        destOptions.inSampleSize = srcOptions.inSampleSize;
        destOptions.inScaled = srcOptions.inScaled;
        destOptions.inScreenDensity = srcOptions.inScreenDensity;
        destOptions.inTargetDensity = srcOptions.inTargetDensity;
        destOptions.inTempStorage = srcOptions.inTempStorage;
        if (Build.VERSION.SDK_INT >= 10) copyOptions10(srcOptions, destOptions);
        if (Build.VERSION.SDK_INT >= 11) copyOptions11(srcOptions, destOptions);
    }

    @TargetApi(10)
    private void copyOptions10(Options srcOptions, Options destOptions) {
        destOptions.inPreferQualityOverSpeed = srcOptions.inPreferQualityOverSpeed;
    }

    @TargetApi(11)
    private void copyOptions11(Options srcOptions, Options destOptions) {
        destOptions.inBitmap = srcOptions.inBitmap;
        destOptions.inMutable = srcOptions.inMutable;
    }

    public String getImageKey() {
        return imageKey;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getOriginalImageUri() {
        return originalImageUri;
    }

    public ImageSize getTargetSize() {
        return targetSize;
    }

    public ImageScaleType getImageScaleType() {
        return imageScaleType;
    }

    public ViewScaleType getViewScaleType() {
        return viewScaleType;
    }

    public ImageDownloader getDownloader() {
        return downloader;
    }

    public Object getExtraForDownloader() {
        return extraForDownloader;
    }

    public boolean shouldConsiderExifParams() {
        return considerExifParams;
    }

    public Options getDecodingOptions() {
        return decodingOptions;
    }
}
