package com.violet.core.provider;

import android.content.ContentResolver;
import android.content.Context;

/**
 * Created by kan212 on 2018/6/12.
 * ContentResolver可以不同URI操作不同的ContentProvider中的数据，外部进程可以通过ContentResolver与ContentProvider进行交互
 */

public abstract class CoreContentResolve extends ContentResolver{

    public CoreContentResolve(Context context) {
        super(context);
    }
}
