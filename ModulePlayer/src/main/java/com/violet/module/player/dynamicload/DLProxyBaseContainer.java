package com.violet.module.player.dynamicload;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by kan212 on 2018/9/4.
 */

public class DLProxyBaseContainer <T extends DLStaticProxyBase> extends FrameLayout{
    public DLProxyBaseContainer(@NonNull Context context) {
        super(context);
    }

    public DLProxyBaseContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DLProxyBaseContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
