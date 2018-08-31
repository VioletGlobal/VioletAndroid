package com.violet.imageloader.core.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.violet.imageloader.core.assist.ImageScaleType;
import com.violet.imageloader.core.assist.ImageSize;
import com.violet.imageloader.core.downloader.ImageDownloader;
import com.violet.imageloader.utils.ImageSizeUtils;
import com.violet.imageloader.utils.IoUtils;
import com.violet.imageloader.utils.L;
import com.violet.net.http.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kan212 on 2018/8/29.
 * 实现了ImageDecoder。调用ImageDownloader获取图片，然后根据ImageDecodingInfo或图片 Exif 信息处理图片转换为 Bitmap
 */

public class BaseImageDecoder implements ImageDecoder {

    protected static final String LOG_SUBSAMPLE_IMAGE = "Subsample original image (%1$s) to %2$s (scale = %3$d) [%4$s]";
    protected static final String LOG_SCALE_IMAGE = "Scale subsampled image (%1$s) to %2$s (scale = %3$.5f) [%4$s]";
    protected static final String LOG_ROTATE_IMAGE = "Rotate image on %1$d\u00B0 [%2$s]";
    protected static final String LOG_FLIP_IMAGE = "Flip image horizontally [%s]";
    protected static final String ERROR_NO_IMAGE_STREAM = "No stream for image [%s]";
    protected static final String ERROR_CANT_DECODE_IMAGE = "Image can't be decoded [%s]";

    protected final boolean loggingEnabled;

    /**
     * @param loggingEnabled Whether debug logs will be written to LogCat.
     */
    public BaseImageDecoder(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    /**
     * 调用ImageDownloader获取图片，再调用defineImageSizeAndRotation(…)函数得到图片的相关信息，
     * 调用prepareDecodingOptions(…)得到图片缩放的比例，调用BitmapFactory.decodeStream将 InputStream 转换为 Bitmap，
     * 最后调用considerExactScaleAndOrientatiton(…)根据参数将图片放大、翻转、旋转为合适的样子返回。
     *
     * @param decodingInfo
     * @return
     * @throws IOException
     */
    @Override
    public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {
        Bitmap decodedBitmap;
        ImageFileInfo imageInfo;
        //从downloader中获取inputstream
        InputStream imageStream = getImageStream(decodingInfo);
        if (null == imageStream) {
            L.e(ERROR_NO_IMAGE_STREAM, decodingInfo.getImageKey());
            return null;
        }
        try {
            //得到图片真实大小以及 Exif 信息(设置考虑 Exif 的条件下)。
            imageInfo = defineImageSizeAndRotation(imageStream, decodingInfo);
            imageStream = resetStream(imageStream,decodingInfo);
            BitmapFactory.Options decodingOptions = prepareDecodingOptions(imageInfo.imageSize, decodingInfo);
            decodedBitmap = BitmapFactory.decodeStream(imageStream,null,decodingOptions);
        }finally {
            IoUtils.closeSilently(imageStream);
        }
        if (decodedBitmap == null) {
            L.e(ERROR_CANT_DECODE_IMAGE, decodingInfo.getImageKey());
        } else {
            decodedBitmap = considerExactScaleAndOrientatiton(decodedBitmap, decodingInfo, imageInfo.exif.rotation,
                    imageInfo.exif.flipHorizontal);
        }
        return decodedBitmap;
    }

    /**
     * 根据参数将图片放大、翻转、旋转为合适的样子返回。
     * @param subsampledBitmap
     * @param decodingInfo
     * @param rotation
     * @param flipHorizontal
     * @return
     */
    private Bitmap considerExactScaleAndOrientatiton(Bitmap subsampledBitmap, ImageDecodingInfo decodingInfo, int rotation, boolean flipHorizontal) {
        Matrix m = new Matrix();
        // Scale to exact size if need
        ImageScaleType scaleType = decodingInfo.getImageScaleType();
        if (scaleType == ImageScaleType.EXACTLY || scaleType == ImageScaleType.EXACTLY_STRETCHED){
            ImageSize srcSize = new ImageSize(subsampledBitmap.getWidth(),subsampledBitmap.getHeight(),rotation);
            float scale = ImageSizeUtils.computeImageScale(srcSize, decodingInfo.getTargetSize(), decodingInfo
                    .getViewScaleType(), scaleType == ImageScaleType.EXACTLY_STRETCHED);
            if (Float.compare(scale, 1f) != 0){
                m.setScale(scale,scale);
                if (loggingEnabled) {
                    L.d(LOG_SCALE_IMAGE, srcSize, srcSize.scale(scale), scale, decodingInfo.getImageKey());
                }
            }
        }
        // Flip bitmap if need
        if (flipHorizontal){
            m.postScale(-1,1);
            if (loggingEnabled) L.d(LOG_FLIP_IMAGE, decodingInfo.getImageKey());
        }
        // Rotate bitmap if need
        if (rotation != 0){
            m.postRotate(rotation);
            if (loggingEnabled) L.d(LOG_ROTATE_IMAGE, rotation, decodingInfo.getImageKey());
        }
        Bitmap finalBitmap = Bitmap.createBitmap(subsampledBitmap,0,0,subsampledBitmap.getWidth(),subsampledBitmap.getHeight(),m,true);
        if (finalBitmap != subsampledBitmap) {
            subsampledBitmap.recycle();
        }
        return finalBitmap;
    }

    /**
     * 得到图片缩放的比例
     * @param imageSize
     * @param decodingInfo
     * @return
     */
    private BitmapFactory.Options prepareDecodingOptions(ImageSize imageSize, ImageDecodingInfo decodingInfo) {
        ImageScaleType scaleType = decodingInfo.getImageScaleType();
        int scale;
        //如果scaleType等于ImageScaleType.NONE，则缩放比例为 1；
        if (scaleType == ImageScaleType.NONE){
            scale = 1;
        }
        //如果scaleType等于ImageScaleType.NONE_SAFE，则缩放比例为
        // (int)Math.ceil(Math.max((float)srcWidth / maxWidth, (float)srcHeight / maxHeight))；
        else if (scaleType == ImageScaleType.NONE_SAFE) {
            scale = ImageSizeUtils.computeMinImageSampleSize(imageSize);
        }
        //调用ImageSizeUtils.computeImageSampleSize(…)计算缩放比例。
        // 在 computeImageSampleSize(…) 中
        else {
            ImageSize targetSize = decodingInfo.getTargetSize();
            boolean powerOf2 = scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2;
            scale = ImageSizeUtils.computeImageSampleSize(imageSize, targetSize, decodingInfo.getViewScaleType(), powerOf2);
        }
        if (scale > 1 && loggingEnabled) {
            L.d(LOG_SUBSAMPLE_IMAGE, imageSize, imageSize.scaleDown(scale), scale, decodingInfo.getImageKey());
        }
        BitmapFactory.Options decodingOptions = decodingInfo.getDecodingOptions();
        decodingOptions.inSampleSize = scale;
        return decodingOptions;
    }

    /**
     *
     * @param imageStream
     * @param decodingInfo
     * @return
     */
    private InputStream resetStream(InputStream imageStream, ImageDecodingInfo decodingInfo) throws IOException {
        if (imageStream.markSupported()){
            try {
                imageStream.reset();
                return imageStream;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        IOUtils.closeQuietly(imageStream);
        return getImageStream(decodingInfo);
    }

    /**
     * 得到图片真实大小以及 Exif 信息(设置考虑 Exif 的条件下)。
     *
     * @param imageStream
     * @param decodingInfo
     * @return
     */
    private ImageFileInfo defineImageSizeAndRotation(InputStream imageStream, ImageDecodingInfo decodingInfo) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);
        ExifInfo exifInfo;
        String imageUri = decodingInfo.getImageUri();
        //是否需要考虑图片 Exif 信息
        if (decodingInfo.shouldConsiderExifParams() && canDefineExifParams(imageUri, options.outMimeType)) {
            exifInfo = defineExifOrientation(imageUri);
        }else {
            exifInfo = new ExifInfo();
        }
        return new ImageFileInfo(new ImageSize(options.outWidth,options.outHeight,exifInfo.rotation),exifInfo);
    }

    /**
     * 得到图片 Exif 信息中的翻转以及旋转角度信息。
     *
     * @param imageUri
     * @return
     */
    private ExifInfo defineExifOrientation(String imageUri) {
        int rotation = 0;
        boolean flip = false;
        try {
            //获取图片exif信息
            ExifInterface exifInterface = new ExifInterface(ImageDownloader.Scheme.FILE.crop(imageUri));
            int exifOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    flip = true;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotation = 0;
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ExifInfo(rotation, flip);
    }

    /**
     * @param imageUri
     * @param outMimeType
     * @return
     */
    private boolean canDefineExifParams(String imageUri, String outMimeType) {
        return "image/jpeg".equalsIgnoreCase(outMimeType) && (ImageDownloader.Scheme.ofUri(imageUri) == ImageDownloader.Scheme.FILE);
    }

    /**
     * 从图片下载器获取inputstream
     *
     * @param decodingInfo
     * @return
     * @throws IOException
     */
    private InputStream getImageStream(ImageDecodingInfo decodingInfo) throws IOException {
        return decodingInfo.getDownloader().getStream(decodingInfo.getImageUri(), decodingInfo.getExtraForDownloader());
    }


    protected static class ExifInfo {

        public final int rotation;
        public final boolean flipHorizontal;

        protected ExifInfo() {
            this.rotation = 0;
            this.flipHorizontal = false;
        }

        protected ExifInfo(int rotation, boolean flipHorizontal) {
            this.rotation = rotation;
            this.flipHorizontal = flipHorizontal;
        }
    }

    protected static class ImageFileInfo {

        public final ImageSize imageSize;
        public final ExifInfo exif;

        protected ImageFileInfo(ImageSize imageSize, ExifInfo exif) {
            this.imageSize = imageSize;
            this.exif = exif;
        }
    }

}
