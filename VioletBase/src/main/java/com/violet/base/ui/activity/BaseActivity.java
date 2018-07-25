package com.violet.base.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.violet.core.ui.activity.CoreActivity;
import com.violet.core.util.LogUtil;

/**
 * Activity切换：
 * A-----onPause()
 * B-----onCreate()
 * B-----onStart()
 * B-----onResume()
 * A-----onStop()
 * 点击Back键返回：
 * B-----onPause()
 * A-----onRestart()
 * A-----onStart()
 * A-----onResume()
 * B-----onStope()
 * B-----onDestroy()
 */

public abstract class BaseActivity extends CoreActivity {

    /**
     * 第一次被创建时回调的方法，加载当前Activity的布局或者初始化View或者加载数据到集合等。
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("onCreate");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 当Activity能够被用户看到时调用的方法
     */
    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d("onStart");
    }

    /**
     * 当Activity能够与用户交互时回调的方法，或者称为获取用户焦点时回调的方法。
     */
    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("onResume");
    }

    /**
     * 当Activity失去用户焦点时回调的方法（不能与用户交互，暂停方法）启动了其它的Activity 时就会回调。
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 当Activity 完全被遮挡的时候回调的方法
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 当Activity重新被启动时回调的方法
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * 当Activity 被销毁时回调的方法
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * LaunchMode:
     * standard
     * 在这种模式下启动的activity可以被多次实例化
     * singleTop {@link Intent.FLAG_ACTIVITY_SINGLE_TOP}
     * 实例已经存在于任务桟的桟顶，那么再启动这个Activity时，不会创建新的实例，而是重用位于栈顶的那个实例，
     * 并且会调用该实例的onNewIntent()方法将Intent对象传递到这个实例中,不在栈顶的时候需要重新创建
     * singleTask
     * 如果在栈中已经有该Activity的实例，就重用该实例(会调用实例的onNewIntent())。重用时，会让该实例回到栈顶，
     * 因此在它上面的实例将会被移除栈。如果栈中不存在该实例，将会创建新的实例放入栈中
     * singleInstance
     * 总是在新的任务中开启，并且这个新的任务中有且只有这一个实例
     * 再次启动该activity的实例时，会重用已存在的任务和实例。并且会调用这个实例的onNewIntent()方法
     *
     * @param intent
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

}
