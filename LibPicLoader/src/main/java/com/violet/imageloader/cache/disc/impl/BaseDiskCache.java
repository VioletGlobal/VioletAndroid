package com.violet.imageloader.cache.disc.impl;

import android.graphics.Bitmap;

import com.violet.imageloader.cache.disc.DiskCache;
import com.violet.imageloader.cache.disc.naming.FileNameGenerator;
import com.violet.imageloader.core.DefaultConfigurationFactory;
import com.violet.imageloader.utils.IoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kan212 on 2018/8/29.
 * 一个无大小限制的本地图片缓存，实现了DiskCache主要函数的抽象类。
 * 图片缓存在cacheDir文件夹内，当cacheDir不可用时，则使用备库reserveCacheDir
 */

public abstract class BaseDiskCache implements DiskCache {

    /** {@value */
    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 Kb
    /** {@value */
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    /** {@value */
    public static final int DEFAULT_COMPRESS_QUALITY = 100;

    private static final String ERROR_ARG_NULL = " argument must be not null";
    private static final String TEMP_IMAGE_POSTFIX = ".tmp";

    protected final File cacheDir;
    //备用
    protected final File reserveCacheDir;

    protected final FileNameGenerator fileNameGenerator;

    protected int bufferSize = DEFAULT_BUFFER_SIZE;

    protected Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
    protected int compressQuality = DEFAULT_COMPRESS_QUALITY;

    /** @param cacheDir Directory for file caching */
    public BaseDiskCache(File cacheDir) {
        this(cacheDir, null);
    }

    /**
     * @param cacheDir        Directory for file caching
     * @param reserveCacheDir null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
     */
    public BaseDiskCache(File cacheDir, File reserveCacheDir) {
        this(cacheDir, reserveCacheDir, DefaultConfigurationFactory.createFileNameGenerator());
    }

    /**
     * @param cacheDir          Directory for file caching
     * @param reserveCacheDir   null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
     * @param fileNameGenerator {@linkplain FileNameGenerator
     *                          Name generator} for cached files
     */
    public BaseDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        if (cacheDir == null) {
            throw new IllegalArgumentException("cacheDir" + ERROR_ARG_NULL);
        }
        if (fileNameGenerator == null) {
            throw new IllegalArgumentException("fileNameGenerator" + ERROR_ARG_NULL);
        }

        this.cacheDir = cacheDir;
        this.reserveCacheDir = reserveCacheDir;
        this.fileNameGenerator = fileNameGenerator;
    }

    @Override
    public File getDirectory() {
        return cacheDir;
    }

    @Override
    public File get(String imageUri) {
        return getFile(imageUri);
    }

    /**
     * 根据 imageUri 和 fileNameGenerator得到文件名，返回cacheDir内该文件，若cacheDir不可用，
     * 则使用备库reserveCacheDir。
     * @param imageUri
     * @return
     */
    protected  File getFile(String imageUri){
        String fileName = fileNameGenerator.generate(imageUri);
        File dir = cacheDir;
        if (!cacheDir.exists() && !cacheDir.mkdirs()){
            if (reserveCacheDir != null && (reserveCacheDir.exists() || reserveCacheDir.mkdirs())) {
                dir = reserveCacheDir;
            }
        }
        return new File(dir,fileName);
    }

    /**
     * 先根据imageUri得到目标文件，将imageStream先写入与目标文件同一文件夹的 .tmp 结尾的临时文件内，
     * 若未被listener取消且写入成功则将临时文件重命名为目标文件并返回 true，否则删除临时文件并返回 false。
     *
     * @param imageUri
     * @param imageStream
     * @param listener
     * @return
     * @throws IOException
     */
    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        File imageFile = getFile(imageUri);
        File tempFile = new File(imageFile.getAbsolutePath() + TEMP_IMAGE_POSTFIX);
        boolean loaded = false;
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile),bufferSize);
            try {
                loaded = IoUtils.copyStream(imageStream,os,listener,bufferSize);
            }finally {
                IoUtils.closeSilently(os);
            }
        }finally {
            if (loaded && !tempFile.renameTo(imageFile)){
                loaded = false;
            }
            if (!loaded){
                tempFile.delete();
            }
        }
        return loaded;
    }

    /**
     * 先根据imageUri得到目标文件，通过Bitmap.compress(…)函数将bitmap先写入与目标文件同一文件夹的
     * .tmp 结尾的临时文件内，若写入成功则将临时文件重命名为目标文件并返回 true，否则删除临时文件并返回 false。
     * @param imageUri
     * @param bitmap
     * @return
     * @throws IOException
     */
    @Override
    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
        File imageFile = getFile(imageUri);
        File tmpFile = new File(imageFile.getAbsolutePath() + TEMP_IMAGE_POSTFIX);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile), bufferSize);
        boolean savedSuccessfully = false;
        try {
            savedSuccessfully = bitmap.compress(compressFormat,compressQuality,os);
        }finally {
            IoUtils.closeSilently(os);
            if (savedSuccessfully && !tmpFile.renameTo(imageFile)) {
                savedSuccessfully = false;
            }
            if (!savedSuccessfully) {
                tmpFile.delete();
            }
        }
        bitmap.recycle();
        return savedSuccessfully;
    }

    @Override
    public boolean remove(String imageUri) {
        return getFile(imageUri).delete();
    }

    @Override
    public void close() {

    }

    @Override
    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
    }

    public void setCompressQuality(int compressQuality) {
        this.compressQuality = compressQuality;
    }
}
