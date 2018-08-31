package com.violet.core.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kan212 on 2018/4/15.
 */

public abstract class CoreFragment extends Fragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在页面重启时，Fragment会被保存恢复，而此时再加载Fragment会重复加载，导致重叠 ;
        if(savedInstanceState == null){
            // 或者 if(findFragmentByTag(mFragmentTag) == null)
            // 正常情况下去 加载根Fragment
        }
    }

    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null && getActivity().isFinishing()) {
            return null;
        }
        View rootView = inflater.inflate(getLayoutId(), container, false);
        initView(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && getActivity().isFinishing()) {
            return;
        }
        if (savedInstanceState != null) {
            if (getActivity() != null && getActivity().getIntent() != null) {
                Intent intent = getActivity().getIntent();
                intent.putExtras(savedInstanceState);
                initData(intent);
            }
        } else {
            if (getActivity() != null && getActivity().getIntent() != null) {
                initData(getActivity().getIntent());
            } else {
                initData(null);
            }
        }
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(View parent);

    protected abstract void initData(Intent intent);
}
