package com.violet.imageloader.utils;

import com.violet.imageloader.core.assist.ImageSize;

import java.util.Comparator;

/**
 * Created by kan212 on 2018/8/29.
 * 内存缓存工具类。可用于根据 uri 生成内存缓存 key，缓存 key 比较，
 * 根据 uri 得到所有相关的 key 或图片，删除某个 uri 的内存缓存。
 * generateKey(String imageUri, ImageSize targetSize)
 * 根据 uri 生成内存缓存 key，key 规则为[imageUri]_[width]x[height]。
 */

public final class MemoryCacheUtils {

    private static final String URI_AND_SIZE_SEPARATOR = "_";
    private static final String WIDTH_AND_HEIGHT_SEPARATOR = "x";

    private MemoryCacheUtils() {
    }

    public static String generateKey(String imageUri, ImageSize targetSize) {
        return new StringBuilder(imageUri).append(URI_AND_SIZE_SEPARATOR).append(targetSize.getWidth()).append(WIDTH_AND_HEIGHT_SEPARATOR).append(targetSize.getHeight()).toString();
    }

    public static Comparator<String> createFuzzyKeyComparator() {
        return new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                String imageUri1 = key1.substring(0, key1.lastIndexOf(URI_AND_SIZE_SEPARATOR));
                String imageUri2 = key2.substring(0, key1.lastIndexOf(URI_AND_SIZE_SEPARATOR));
                return imageUri1.compareTo(imageUri2);
            }
        };
    }
}
