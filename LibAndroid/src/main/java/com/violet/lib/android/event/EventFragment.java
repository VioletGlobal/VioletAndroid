package com.violet.lib.android.event;

import android.content.Intent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.base.ui.fragment.BaseFragment;
import com.violet.lib.android.LibAndroidRouter;
import com.violet.lib.android.R;

/**
 * Created by kan212 on 2018/6/5.
 * 事件分发的测试
 */

@Route(path = LibAndroidRouter.libRouter.LIB_ANDROID_EVENT)
public class EventFragment extends BaseFragment{

    @Override
    protected int getLayoutId() {
        return R.layout.lib_android_fragment_event;
    }

    @Override
    protected void initView(View parent) {

    }

    @Override
    protected void initData(Intent intent) {

    }
}
