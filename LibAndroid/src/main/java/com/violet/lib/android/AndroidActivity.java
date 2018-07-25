package com.violet.lib.android;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.violet.base.bean.DemoActivityBean;
import com.violet.base.grape.router.RouterUrl;
import com.violet.base.grape.router.VioletRouter;
import com.violet.base.ui.activity.BaseDemoActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kan212 on 2018/6/4.
 */
@Route(path = RouterUrl.AndroidRouter.ANDROID_ACTIVITY)
public class AndroidActivity extends BaseDemoActivity {

    @Override
    public List<DemoActivityBean> getDataList() {
        return Arrays.asList(
                new DemoActivityBean(R.string.mAndroid_xml, LibAndroidRouter.libRouter.LIB_ANDROID_XML),
                new DemoActivityBean(R.string.mAndroid_design, LibAndroidRouter.libRouter.LIB_ANDROID_DESGIN),
                new DemoActivityBean(R.string.mAndroid_event, LibAndroidRouter.libRouter.LIB_ANDROID_EVENT),
                new DemoActivityBean(R.string.mAndroid_paint, LibAndroidRouter.libRouter.LIB_ANDROID_PAINT));
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    protected void initData(Intent intent) {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VioletRouter.go2ActivityWithFragment(RouterUrl.InnerRouter.INNER_ACTIVITY_FRAGMENT,mAdapter.getData().get(position).mUrl);
            }
        });
    }
}
