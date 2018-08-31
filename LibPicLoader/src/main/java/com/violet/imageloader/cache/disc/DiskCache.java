package com.violet.imageloader.cache.disc;

import android.graphics.Bitmap;

import com.violet.imageloader.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kan212 on 2018/8/29.
 * 图片的磁盘缓存接口。
 */

public interface DiskCache {
    //得到磁盘缓存的根目录。
    File getDirectory();
    //根据原始图片的 uri 去获取缓存图片的文件。
    File get(String imageUri);
    //保存 imageStream 到磁盘中，listener 表示保存进度且可在其中取消某些段的保存。
    boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException;

    //保存图片到磁盘。
    boolean save(String imageUri, Bitmap bitmap) throws IOException;

    //根据图片 uri 删除缓存图片。
    boolean remove(String imageUri);

    //关闭磁盘缓存，并释放资源。
    void close();

    //清空磁盘缓存。
    void clear();

}
