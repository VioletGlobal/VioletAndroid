package com.violet.base.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.violet.base.R;
import com.violet.base.bean.DemoActivityBean;
import com.violet.base.ui.adapter.DemoAdapter;
import com.violet.core.ui.activity.CoreActivity;

import java.util.List;
/**
 * Created by kan212 on 2018/4/18.
 */

public abstract class BaseDemoActivity extends CoreActivity{

    public RecyclerView mRecyclerView;
    public DemoAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_demo;
    }

    @Override
    protected void initView(View parent) {
        mRecyclerView = (RecyclerView) parent.findViewById(R.id.recycler_main);
        mAdapter = new DemoAdapter(getDataList());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        mRecyclerView.setAdapter(mAdapter);
    }

    public abstract List<DemoActivityBean> getDataList();
}
