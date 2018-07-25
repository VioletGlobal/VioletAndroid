package com.violet.lib.java.interview;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.violet.base.bean.DemoActivityBean;
import com.violet.base.grape.router.RouterUrl;
import com.violet.base.grape.router.VioletRouter;
import com.violet.base.ui.activity.BaseDemoActivity;
import com.violet.lib.java.R;
import com.violet.lib.java.grape.LibJavaRouter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kan212 on 2018/4/20.
 */
@Route(path = RouterUrl.JavaRouter.LIB_JAVA_ACTIVITY)
public class InterviewActivity extends BaseDemoActivity {

    @Override
    public List<DemoActivityBean> getDataList() {
        return Arrays.asList(
                new DemoActivityBean(R.string.java_equals, LibJavaRouter.libJavaRouter.LIB_JAVA_FRAGMENT),
                new DemoActivityBean(R.string.java_algorithm, LibJavaRouter.libJavaRouter.LIB_ALGORITHM_FRAGMENT));
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {

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
