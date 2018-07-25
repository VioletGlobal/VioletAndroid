package com.violet.core.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.violet.core.R;

/**
 * Created by kan212 on 2018/4/12.
 */

public abstract class CoreActivity extends SwipeBackActivity {

    protected Toolbar mToolbar;
    protected View mToolbarLine;
    protected TextView mTvCenterTitle;
    protected FrameLayout mContentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_base);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            mContentLayout.addView(LayoutInflater.from(this).inflate(layoutId, null));
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarLine = findViewById(R.id.divider_line);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);

        initView(mContentLayout);
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
    }

    protected void hideToolbar() {
        if (mToolbarLine != null) {
            mToolbarLine.setVisibility(View.GONE);
        }
        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    public static void initWindow(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        //透明状态栏
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }

        // 防止第一次播放视频，闪黑屏幕
        window.setFormat(PixelFormat.TRANSLUCENT);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //[4.4, 5.0)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            //[5.0, +)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            try {
                window.setStatusBarColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void addFragment(int layoutId, Fragment fragment, boolean addToBackStack, String fragmentTag, int enterAnim, int exitAnim) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (enterAnim != 0 && exitAnim != 0) {
            transaction.setCustomAnimations(enterAnim, exitAnim);
        }
        if (fragmentTag == null) {
            transaction.add(layoutId, fragment);
        } else {
            transaction.add(layoutId, fragment, fragmentTag);
        }
        transaction.commit();
    }

    protected void addFragment(int layoutId, Fragment fragment, boolean addToBackStack, String fragmentTag) {
        addFragment(layoutId, fragment, addToBackStack, fragmentTag, 0, 0);
    }

    protected void addFragment(int layoutId, Fragment fragment, int enterAnim, int exitAnim) {
        addFragment(layoutId, fragment, false, null, enterAnim, exitAnim);
    }

    protected void addFragment(int layoutId, Fragment fragment, boolean addToBackStack) {
        addFragment(layoutId, fragment, addToBackStack, null);
    }

    protected void addFragment(int layoutId, Fragment fragment, String tag) {
        addFragment(layoutId, fragment, false, tag);
    }

    public void addFragment(int layoutId, Fragment fragment) {
        addFragment(layoutId, fragment, false);
    }

    protected Fragment findFragment(int layoutId) {
        FragmentManager fm = getSupportFragmentManager();
        return fm.findFragmentById(layoutId);
    }

    protected void removeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    protected void hideFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    protected void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    protected void replaceFragment(Fragment fragment) {
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

    protected boolean isFinishOrDestroy() {
        boolean isOver = isFinishing();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            isOver = isDestroyed();
        }
        return isOver;
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View parent);

    protected abstract void initToolbar(Toolbar toolbar);

    protected abstract void initData(Intent intent);

    protected void initCenterTitle(TextView centerTitle) {
    }

    protected void afterSetSupportActionBar(Toolbar toolbar) {
    }
}
