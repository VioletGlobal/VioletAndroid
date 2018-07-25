package com.violet.core.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.violet.core.R;
/**
 * Created by kan212 on 2018/4/18.
 */

public abstract class CoreFragmentActivity extends SwipeBackActivity {

    protected Toolbar mToolbar;
    protected View mToolbarLine;
    protected TextView mTvCenterTitle;
    protected FrameLayout mContentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_base);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarLine = findViewById(R.id.divider_line);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);

        if (savedInstanceState != null) {
            Intent intent = getIntent();
            intent.putExtras(savedInstanceState);
            initData(intent);
        } else {
            initData(getIntent());
        }
        initToolbar(mToolbar);//Toolbar的setTitle方法要在setSupportActionBar(toolbar)之前调用，否则不起作用
        initCenterTitle(mTvCenterTitle);
        setSupportActionBar(mToolbar);
        afterSetSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        replaceFragment(getFragment());
    }

    protected void replaceFragment(Fragment fragment) {
        if (null == fragment){
            return;
        }
        replaceFragment(R.id.layout_content, fragment);
    }

    protected void replaceFragment(int layoutId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(layoutId, fragment);
        transaction.commit();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    protected abstract Fragment getFragment();

    protected abstract void initToolbar(Toolbar toolbar);

    protected abstract void initData(Intent intent);

    protected void initCenterTitle(TextView centerTitle) {
    }

    protected void afterSetSupportActionBar(Toolbar toolbar) {
    }
}

