package com.violet.lib.algor;

import android.content.Intent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.base.ui.fragment.BaseFragment;
import com.violet.lib.java.R;
import com.violet.lib.java.grape.LibJavaRouter;

/**
 * Created by kan212 on 2018/5/14.
 */
@Route(path = LibJavaRouter.libJavaRouter.LIB_ALGORITHM_FRAGMENT)
public class AlgorithmFragment extends BaseFragment{

    @Override
    protected int getLayoutId() {
        return R.layout.java_fragment_algorithm;
    }

    @Override
    protected void initView(View parent) {

    }

    @Override
    protected void initData(Intent intent) {

    }

}


