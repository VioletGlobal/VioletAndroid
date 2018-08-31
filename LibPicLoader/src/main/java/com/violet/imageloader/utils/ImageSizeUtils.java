package com.violet.imageloader.utils;

import android.opengl.GLES10;

import com.violet.imageloader.core.assist.ImageSize;
import com.violet.imageloader.core.assist.ViewScaleType;
import com.violet.imageloader.core.imageAware.ImageAware;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by kan212 on 2018/8/30.
 */

public class ImageSizeUtils {

    private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;

    private static ImageSize maxBitmapSize;

    static {
        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        int maxBitmapDimension = Math.max(maxTextureSize[0], DEFAULT_MAX_BITMAP_DIMENSION);
        maxBitmapSize = new ImageSize(maxBitmapDimension, maxBitmapDimension);
    }

    private ImageSizeUtils() {
    }

    /**
     * Defines target size for image aware view. Size is defined by target
     *
     * @param imageAware
     * @param maxImageSize
     * @return
     */
    public static ImageSize defineTargetSizeForView(ImageAware imageAware, ImageSize maxImageSize) {
        int width = imageAware.getWidth();
        if (width <= 0) width = maxImageSize.getWidth();
        int height = imageAware.getHeight();
        if (height <= 0) height = maxImageSize.getHeight();

        return new ImageSize(width, height);
    }


    /**
     * 如果viewScaleType等于ViewScaleType.FIT_INSIDE；
     * 1.1 如果scaleType等于ImageScaleType.IN_SAMPLE_POWER_OF_2，则缩放比例从 1 开始不断 *2 直到宽或高小于最大尺寸；
     * 1.2 否则取宽和高分别与最大尺寸比例中较大值，即Math.max(srcWidth / targetWidth, srcHeight / targetHeight)
     * 如果scaleType等于ViewScaleType.CROP；
     * 2.1 如果scaleType等于ImageScaleType.IN_SAMPLE_POWER_OF_2，则缩放比例从 1 开始不断 *2 直到宽和高都小于最大尺寸。
     * 2.2 否则取宽和高分别与最大尺寸比例中较小值，即Math.min(srcWidth / targetWidth, srcHeight / targetHeight)。
     * 3 最后判断宽和高是否超过最大值，如果是 *2 或是 +1 缩放。
     *
     * @param srcSize
     * @param targetSize
     * @param viewScaleType
     * @param powerOf2Scale
     * @return
     */
    public static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType,
                                             boolean powerOf2Scale) {
        final int srcWidth = srcSize.getWidth();
        final int srcHeight = srcSize.getHeight();
        final int targetWidth = targetSize.getWidth();
        final int targetHeight = targetSize.getHeight();
        int scale = 1;
        switch (viewScaleType) {
            case FIT_INSIDE:
                if (powerOf2Scale) {
                    final int halfWidth = srcWidth / 2;
                    final int halfHeight = srcHeight / 2;
                    while ((halfWidth / scale) > targetWidth || (halfHeight / scale) > targetHeight) { // ||
                        scale *= 2;
                    }
                } else {
                    scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
                }
                break;
            case CROP:
                if (powerOf2Scale) {
                    final int halfWidth = srcWidth / 2;
                    final int halfHeight = srcHeight / 2;
                    while ((halfWidth / scale) > targetWidth && (halfHeight / scale) > targetHeight) { // &&
                        scale *= 2;
                    }
                } else {
                    scale = Math.min(srcWidth / targetWidth, srcHeight / targetHeight); // min
                }
                break;
        }
        if (scale < 1) {
            scale = 1;
        }
        scale = considerMaxTextureSize(srcWidth, srcHeight, scale, powerOf2Scale);
        return scale;
    }

    /**
     * @param srcWidth
     * @param srcHeight
     * @param scale
     * @param powerOf2
     * @return
     */
    private static int considerMaxTextureSize(int srcWidth, int srcHeight, int scale, boolean powerOf2) {
        final int maxWidth = maxBitmapSize.getWidth();
        final int maxHeight = maxBitmapSize.getHeight();
        while ((srcWidth / scale) > maxWidth || (srcHeight / scale) > maxHeight) {
            if (powerOf2) {
                scale *= 2;
            } else {
                scale++;
            }
        }
        return scale;
    }

    /**
     * @param srcSize
     * @return
     */
    public static int computeMinImageSampleSize(ImageSize srcSize) {
        final int srcWidth = srcSize.getWidth();
        final int srcHeight = srcSize.getHeight();
        final int targetWidth = maxBitmapSize.getWidth();
        final int targetHeight = maxBitmapSize.getHeight();

        final int widthScale = (int) Math.ceil((float) srcWidth / targetWidth);
        final int heightScale = (int) Math.ceil((float) srcHeight / targetHeight);

        return Math.max(widthScale, heightScale); // max
    }

    /**
     * Computes scale of target size (<b>targetSize</b>) to source size (<b>srcSize</b>).<br />
     * <br />
     * <b>Examples:</b><br />
     * <p/>
     * <pre>
     * srcSize(40x40), targetSize(10x10) -> scale = 0.25
     *
     * srcSize(10x10), targetSize(20x20), stretch = false -> scale = 1
     * srcSize(10x10), targetSize(20x20), stretch = true  -> scale = 2
     *
     * srcSize(100x100), targetSize(20x40), viewScaleType = FIT_INSIDE -> scale = 0.2
     * srcSize(100x100), targetSize(20x40), viewScaleType = CROP       -> scale = 0.4
     * </pre>
     *
     * @param srcSize       Source (image) size
     * @param targetSize    Target (view) size
     * @param viewScaleType {@linkplain ViewScaleType Scale type} for placing image in view
     * @param stretch       Whether source size should be stretched if target size is larger than source size. If <b>false</b>
     *                      then result scale value can't be greater than 1.
     * @return Computed scale
     */
    public static float computeImageScale(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType,
                                          boolean stretch) {
        final int srcWidth = srcSize.getWidth();
        final int srcHeight = srcSize.getHeight();
        final int targetWidth = targetSize.getWidth();
        final int targetHeight = targetSize.getHeight();

        final float widthScale = (float) srcWidth / targetWidth;
        final float heightScale = (float) srcHeight / targetHeight;

        final int destWidth;
        final int destHeight;
        if ((viewScaleType == ViewScaleType.FIT_INSIDE && widthScale >= heightScale) || (viewScaleType == ViewScaleType.CROP && widthScale < heightScale)) {
            destWidth = targetWidth;
            destHeight = (int) (srcHeight / widthScale);
        } else {
            destWidth = (int) (srcWidth / heightScale);
            destHeight = targetHeight;
        }

        float scale = 1;
        if ((!stretch && destWidth < srcWidth && destHeight < srcHeight) || (stretch && destWidth != srcWidth && destHeight != srcHeight)) {
            scale = (float) destWidth / srcWidth;
        }
        return scale;
    }
}
