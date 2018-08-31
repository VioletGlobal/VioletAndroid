package com.violet.imageloader.cache.memory.impl;

import android.graphics.Bitmap;

import com.violet.imageloader.cache.memory.MemoryCache;

import java.util.Collection;
import java.util.Comparator;

/**
 * Created by kan212 on 2018/8/29.
 * 可以将某些原本不同的 key 看做相等，在 put 时删除这些相等的 key。
 * MemoryCache的装饰者，相当于为MemoryCache添加了一个特性。以一个MemoryCache内存缓存和一个
 * keyComparator 做为构造函数入参。在 put(…) 时判断如果 key 与缓存中已有 key 经过Comparator比较后相等，
 * 则删除之前的元素。
 */

public class FuzzyKeyMemoryCache implements MemoryCache {

    private final MemoryCache cache;
    private final Comparator<String> keyComparator;

    public FuzzyKeyMemoryCache(MemoryCache cache, Comparator<String> keyComparator) {
        this.cache = cache;
        this.keyComparator = keyComparator;
    }

    @Override
    public boolean put(String key, Bitmap value) {
        synchronized (cache) {
            String keyToRemove = null;
            for (String cacheKey : cache.keys()) {
                if (keyComparator.compare(key, cacheKey) == 0) {
                    keyToRemove = cacheKey;
                    break;
                }
            }
            if (null != keyToRemove){
                cache.remove(keyToRemove);
            }
        }
        return cache.put(key,value);
    }

    @Override
    public Bitmap get(String key) {
        return cache.get(key);
    }

    @Override
    public Bitmap remove(String key) {
        return cache.remove(key);
    }

    @Override
    public Collection<String> keys() {
        return cache.keys();
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
