package com.violet.process.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.violet.base.bean.DemoActivityBean;
import com.violet.base.grape.router.RouterUrl;
import com.violet.base.grape.router.VioletRouter;
import com.violet.base.ui.activity.BaseDemoActivity;
import com.violet.process.R;
import com.violet.process.grape.ProcessRouterUrl;

import java.util.Arrays;
import java.util.List;

/**
 * 进程间通讯测试
 * Created by kan212 on 2018/4/18.
 */
@Route(path = RouterUrl.ProcessRouter.PROCESS_ACTIVITY)
public class ProcessActivity extends BaseDemoActivity {

    @Override
    public List<DemoActivityBean> getDataList() {
        return Arrays.asList(
                new DemoActivityBean(R.string.process_binder, ProcessRouterUrl.ProcessFragmentRouter.FRAGMENT_BINDER),
                new DemoActivityBean(R.string.process_provider, ProcessRouterUrl.ProcessFragmentRouter.FRAGMENT_PROVIDER));
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
