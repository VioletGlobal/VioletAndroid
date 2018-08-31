package com.violet.imageloader.cache.disc.impl;

import android.graphics.Bitmap;

import com.violet.imageloader.cache.disc.naming.FileNameGenerator;
import com.violet.imageloader.core.DefaultConfigurationFactory;
import com.violet.imageloader.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kan212 on 2018/8/29.
 * 限制了缓存对象最长存活周期的磁盘缓存，继承自BaseDiskCache。
 * 在 get(…) 时判断如果缓存对象存活时间已经超过设置的最长时间，则删除。在 save(…) 时保存当存时间作为对象的创建时间。
 */

public class LimitedAgeDiskCache extends BaseDiskCache {

    private final long maxFileAge;

    private final Map<File, Long> loadingDates = Collections.synchronizedMap(new HashMap<File, Long>());


    /**
     * @param cacheDir Directory for file caching
     * @param maxAge   Max file age (in seconds). If file age will exceed this value then it'll be removed on next
     *                 treatment (and therefore be reloaded).
     */
    public LimitedAgeDiskCache(File cacheDir, long maxAge) {
        this(cacheDir, null, DefaultConfigurationFactory.createFileNameGenerator(), maxAge);
    }

    /**
     * @param cacheDir Directory for file caching
     * @param maxAge   Max file age (in seconds). If file age will exceed this value then it'll be removed on next
     *                 treatment (and therefore be reloaded).
     */
    public LimitedAgeDiskCache(File cacheDir, File reserveCacheDir, long maxAge) {
        this(cacheDir, reserveCacheDir, DefaultConfigurationFactory.createFileNameGenerator(), maxAge);
    }

    /**
     * @param cacheDir          Directory for file caching
     * @param reserveCacheDir   null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
     * @param fileNameGenerator Name generator for cached files
     * @param maxAge            Max file age (in seconds). If file age will exceed this value then it'll be removed on next
     *                          treatment (and therefore be reloaded).
     */
    public LimitedAgeDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, long maxAge) {
        super(cacheDir, reserveCacheDir, fileNameGenerator);
        this.maxFileAge = maxAge * 1000; // to milliseconds
    }

    @Override
    public File get(String imageUri) {
        File file = super.get(imageUri);
        if (null != file && file.exists()) {
            boolean cached;
            Long loadingDate = loadingDates.get(file);
            if (null == loadingDate) {
                cached = false;
                loadingDate = file.lastModified();
            } else {
                cached = true;
            }
            if (System.currentTimeMillis() - loadingDate > maxFileAge) {
                file.delete();
                loadingDates.remove(file);
            }else if (!cached){
                loadingDates.put(file,loadingDate);
            }
        }
        return file;
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        boolean saved = super.save(imageUri, imageStream, listener);
        rememberUsage(imageUri);
        return saved;
    }

    @Override
    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
        boolean saved = super.save(imageUri, bitmap);
        rememberUsage(imageUri);
        return saved;
    }

    @Override
    public boolean remove(String imageUri) {
        loadingDates.remove(getFile(imageUri));
        return super.remove(imageUri);
    }

    @Override
    public void clear() {
        super.clear();
        loadingDates.clear();
    }

    private void rememberUsage(String imageUri) {
        File file = getFile(imageUri);
        long currentTime = System.currentTimeMillis();
        file.setLastModified(currentTime);
        loadingDates.put(file, currentTime);
    }

}
