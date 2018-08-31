package com.violet.imageloader.cache.memory.impl;

import android.graphics.Bitmap;

import com.violet.imageloader.cache.memory.MemoryCache;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kan212 on 2018/8/29.
 * 限制总字节大小的内存缓存，会在缓存满时优先删除最近最少使用的元素，实现了MemoryCache。
 * LRU(Least Recently Used) 为最近最少使用算法
 */

public class LruMemoryCache implements MemoryCache {

    /**
     * 以new LinkedHashMap<String, Bitmap>(0, 0.75f, true)作为缓存池。LinkedHashMap
     * 第三个参数表示是否需要根据访问顺序(accessOrder)排序，true 表示根据accessOrder排序，
     * 最近访问的跟最新加入的一样放到最后面，false 表示根据插入顺序排序。这里为 true 且缓存满时始终删除第一个元素，
     * 即始终删除最近最少访问的元素。
     */
    private final LinkedHashMap<String, Bitmap> map;

    private final int maxSize;

    /**
     * Size of this cache in bytes
     */
    private int size;

    public LruMemoryCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
    }


    /**
     * 在put(…)函数中通过trimToSize(int maxSize)函数判断总体大小是否超出了上限，
     * 是则删除第缓存池中第一个元素，即最近最少使用的元素，直到总体大小小于上限。
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public final boolean put(String key, Bitmap value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            size += sizeOf(key, value);
            Bitmap previous = map.put(key, value);
            if (null != previous) {
                size -= sizeOf(key, value);
            }
        }
        trimToSize(maxSize);
        return true;
    }

    private void trimToSize(int maxSize) {
        while (true){
            String key;
            Bitmap value;
            synchronized (this){
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                }
                if (size <= maxSize || map.isEmpty()){
                    break;
                }
                Map.Entry<String,Bitmap> toEvict = map.entrySet().iterator().next();
                if (toEvict == null){
                    break;
                }
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= sizeOf(key,value);
            }
        }
    }

    @Override
    public final Bitmap get(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            return map.get(key);
        }
    }

    @Override
    public final Bitmap remove(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            Bitmap previous = map.remove(key);
            if (previous != null) {
                size -= sizeOf(key, previous);
            }
            return previous;
        }
    }

    @Override
    public Collection<String> keys() {
        synchronized (this) {
            return new HashSet<String>(map.keySet());
        }
    }

    @Override
    public void clear() {
        trimToSize(-1); // -1 will evict 0-sized elements
    }

    /**
     * Returns the size {@code Bitmap} in bytes.
     * <p/>
     * An entry's size must not change while it is in the cache.
     */
    private int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public synchronized final String toString() {
        return String.format("LruCache[maxSize=%d]", maxSize);
    }
}
