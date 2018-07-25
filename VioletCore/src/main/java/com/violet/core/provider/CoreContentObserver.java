package com.violet.core.provider;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by kan212 on 2018/6/12.
 * 观察ContentProvider中的数据变化，并将变化通知给外界
 */

public class CoreContentObserver extends ContentObserver{
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public CoreContentObserver(Handler handler) {
        super(handler);
    }
}
