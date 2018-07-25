package com.violet.base;

import com.violet.VioletGrapeApplication;
import com.violet.core.VioletBaseCore;

/**
 * Created by kan212 on 2018/4/13.
 */

public abstract class VioletBaseApplication extends VioletGrapeApplication {

    public static VioletBaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (isMain()) {
            VioletBaseCore.getInstance().init(getApplicationContext());
            initOnMainProcess();
        }
    }

    protected abstract void initOnMainProcess();


}
