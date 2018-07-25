package com.violet.base.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.violet.core.mvp.IBasePresenter;

/**
 * Created by kan212 on 2018/4/17.
 */

public abstract class BaseMvpActivity<T extends IBasePresenter> extends BaseActivity {

    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = createPresenter();
        mPresenter.bind();
        super.onCreate(savedInstanceState);
    }

    protected abstract T createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unBind();
    }
}
