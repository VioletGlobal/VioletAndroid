package com.violet.base.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.violet.base.R;
import com.violet.base.bean.DemoActivityBean;
import com.violet.base.grape.router.VioletRouter;

import java.util.List;

/**
 * Created by kan212 on 2018/4/18.
 */

public class DemoAdapter extends BaseQuickAdapter<DemoActivityBean,BaseViewHolder>{

    public DemoAdapter(@Nullable final List<DemoActivityBean> data) {
        super(R.layout.item_demo, data);
        setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VioletRouter.go2ActivityByUrl(data.get(position).mUrl);
            }
        });
    }


    @Override
    protected void convert(BaseViewHolder helper, DemoActivityBean item) {
        helper.setText(R.id.tv_name,mContext.getString(item.mId));
    }
}
