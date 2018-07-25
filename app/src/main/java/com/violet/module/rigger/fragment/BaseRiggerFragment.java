package com.violet.module.rigger.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.violet.lib.fragment.rigger.annotation.Puppet;

/**
 * Created by kan212 on 2018/5/22.
 */
@Puppet
public abstract class BaseRiggerFragment extends Fragment{
    protected static final String BUNDLE_KEY = "/bundle/key";
    protected View mContentView;
    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContentView = inflater.inflate(getContentView(), container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(savedInstanceState);
    }

    @LayoutRes
    protected abstract int getContentView();

    protected abstract void init(Bundle savedInstanceState);

    protected View findViewById(@IdRes int id) {
        return mContentView.findViewById(id);
    }
}
