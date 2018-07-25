package com.violet.module.main.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.violet.R;
import com.violet.base.grape.router.VioletRouter;
import com.violet.bean.ActivityBean;

import java.util.List;


/**
 * Created by kan212 on 2018/4/17.
 */

public class MainAdapter extends BaseQuickAdapter<ActivityBean,BaseViewHolder>{

    public MainAdapter(@Nullable final List<ActivityBean> data) {
        super(R.layout.item_main, data);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VioletRouter.go2ActivityByUrl(data.get(position).mUrl);
            }
        });
    }


    @Override
    protected void convert(BaseViewHolder helper, ActivityBean item) {
        helper.setText(R.id.tv_name,mContext.getString(item.mId));
    }

}
