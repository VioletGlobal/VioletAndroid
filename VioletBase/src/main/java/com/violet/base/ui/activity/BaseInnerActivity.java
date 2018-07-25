package com.violet.base.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.base.grape.router.RouterUrl;
import com.violet.base.grape.router.VioletRouter;
import com.violet.core.ui.activity.CoreFragmentActivity;
import com.violet.core.util.LogUtil;

import static com.violet.base.grape.router.VioletRouter.VIOLET_ROUTER_FRAGMENT_URL;

/**
 * Created by kan212 on 2018/4/20.
 */

@Route(path = RouterUrl.InnerRouter.INNER_ACTIVITY_FRAGMENT)
public class BaseInnerActivity extends CoreFragmentActivity{

    @Override
    protected Fragment getFragment() {
        return VioletRouter.go2FragmentByUrl(getIntent().getStringExtra(VIOLET_ROUTER_FRAGMENT_URL));
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {

    }

    @Override
    protected void initData(Intent intent) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d("Activity dispatchTouchEvent ");
        return super.dispatchTouchEvent(ev);
    }
}
