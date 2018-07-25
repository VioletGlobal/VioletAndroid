package com.violet.glide;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;

import com.violet.glide.load.engine.bitmap_recycle.ArrayPool;
import com.violet.glide.load.engine.bitmap_recycle.BitmapPool;
import com.violet.glide.load.engine.cache.MemoryCache;
import com.violet.glide.util.Util;

/**
 * Created by kan212 on 2018/4/19.
 */

public class Glide implements ComponentCallbacks2{


    private final MemoryCache memoryCache = null;
    private final BitmapPool bitmapPool= null;
    private final ArrayPool arrayPool = null;


    /**
     * Clears some memory with the exact amount depending on the given level.
     *
     * @see android.content.ComponentCallbacks2#onTrimMemory(int)
     */
    public void trimMemory(int level) {
        // Engine asserts this anyway when removing resources, fail faster and consistently
        Util.assertMainThread();
        // memory cache needs to be trimmed before bitmap pool to trim re-pooled Bitmaps too. See #687.
        memoryCache.trimMemory(level);
        bitmapPool.trimMemory(level);
        arrayPool.trimMemory(level);
    }

    @Override
    public void onTrimMemory(int level) {
        trimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
}
