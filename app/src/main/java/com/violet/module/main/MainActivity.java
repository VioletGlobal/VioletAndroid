package com.violet.module.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.violet.R;
import com.violet.base.grape.router.RouterUrl;
import com.violet.base.ui.activity.BaseMvpActivity;
import com.violet.bean.ActivityBean;
import com.violet.module.main.adapter.MainAdapter;
import com.violet.module.main.contract.IMainContract;
import com.violet.module.main.contract.MainPresenter;
import com.violet.module.main.widget.Rcyclerview;

import java.util.Arrays;
import java.util.List;

@Route(path = RouterUrl.MainRouter.MAIN_ACTIVITY)
public class MainActivity extends BaseMvpActivity<MainPresenter> implements IMainContract.IMainView {

    private Rcyclerview mRecyclerView;
    private MainAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(View parent) {
        mRecyclerView = (Rcyclerview) parent.findViewById(R.id.recycler_main);
        mAdapter = new MainAdapter(getDataList());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
//        ViewCompat.setZ(mRecyclerView,1);
//        CarouselLayoutManager layoutManager = new ThreeCarselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
//        layoutManager.setPostLayoutListener(new ThreeCoverInsertPostLayoutListener());
//        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {
//
//            @Override
//            public void onCenterItemChanged(final int adapterPosition) {
//            }
//        });
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.addOnScrollListener(new CenterScrollListener());
//        mRecyclerView.setHasFixedSize(true);
//
//        CoverInsertPostLayoutListener.reset();
        mRecyclerView.setAdapter(mAdapter);
//        BaseLinkList.Node node = new BaseLinkList().build();
//        node =  BaseLinkList.reverseBy2Rec(node);
//        BaseLinkList.reList(node);

    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    protected void initData(Intent intent) {

    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public Context getContext() {
        return this;
    }

    public List<ActivityBean> getDataList() {
        return Arrays.asList(
                new ActivityBean(R.string.lib_process, RouterUrl.ProcessRouter.PROCESS_ACTIVITY),
                new ActivityBean(R.string.lib_java, RouterUrl.JavaRouter.LIB_JAVA_ACTIVITY),
                new ActivityBean(R.string.lib_fragment, RouterUrl.RiggerRouter.RIGGER_ACTIVITY),
                new ActivityBean(R.string.lib_android, RouterUrl.AndroidRouter.ANDROID_ACTIVITY),
                new ActivityBean(R.string.violet_dagger, RouterUrl.MainRouter.APP_DAGGER),
                new ActivityBean(R.string.open_source, RouterUrl.OpSourceRouter.OPEN_SOURCE_MAIN));
    }

}