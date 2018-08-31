package com.violet.imageloader.cache.disc.impl;

import com.violet.imageloader.cache.disc.naming.FileNameGenerator;

import java.io.File;

/**
 * Created by kan212 on 2018/8/29.
 */

public class UnlimitedDiskCache extends BaseDiskCache{

    public UnlimitedDiskCache(File cacheDir) {
        super(cacheDir);
    }

    public UnlimitedDiskCache(File cacheDir, File reserveCacheDir) {
        super(cacheDir, reserveCacheDir);
    }

    public UnlimitedDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        super(cacheDir, reserveCacheDir, fileNameGenerator);
    }
}
