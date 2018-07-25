package com.violet.base.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.violet.core.mvp.IBasePresenter;

/**
 * Created by kan212 on 2018/4/15.
 */

public abstract class BaseMvpFragment<IP extends IBasePresenter> extends BaseFragment{

    protected IP mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.bind();
    }

    protected abstract IP createPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unBind();
    }

}
